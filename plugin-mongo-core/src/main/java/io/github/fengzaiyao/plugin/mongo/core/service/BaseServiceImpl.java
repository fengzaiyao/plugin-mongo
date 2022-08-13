package io.github.fengzaiyao.plugin.mongo.core.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.github.fengzaiyao.plugin.mongo.core.annotation.EntityLogicId;
import io.github.fengzaiyao.plugin.mongo.core.model.BaseEntity;
import io.github.fengzaiyao.plugin.mongo.core.util.ReflectionsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.beans.Transient;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class BaseServiceImpl<T extends BaseEntity, ID> implements IBaseService<T, ID> {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final Class<T> entityClass = getEntityClass();

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<T> findPage(Criteria criteria, Pageable pageable, Sort sort) {
        return findBasePage(criteria, pageable, sort);
    }

    @Override
    public Page<T> findPage(Criteria criteria, Pageable pageable) {
        return findBasePage(criteria, pageable, null);
    }

    @Override
    public List<T> findAllById(List<ID> ids) {
        Criteria baseCriteria = commonBaseCriteria();
        Criteria criteria = Criteria.where(ReflectionsUtil.convert(T::getId)).in(ids);
        Query query = new Query(new Criteria().andOperator(criteria, baseCriteria));
        return getMongoTemplate().find(query, entityClass);
    }

    @Override
    public List<T> findAllByBzId(List<String> bzIds) {
        Criteria baseCriteria = commonBaseCriteria();
        Criteria criteria = Criteria.where(ReflectionsUtil.convert(T::getBzId)).in(bzIds);
        Query query = new Query(new Criteria().andOperator(criteria, baseCriteria));
        return getMongoTemplate().find(query, entityClass);
    }

    @Override
    public List<T> find(Criteria criteria) {
        Criteria baseCriteria = commonBaseCriteria();
        Query query = new Query(new Criteria().andOperator(criteria, baseCriteria));
        return getMongoTemplate().find(query, entityClass);
    }

    @Override
    public T findByBzId(String bzId) {
        Criteria baseCriteria = commonBaseCriteria();
        Criteria criteria = Criteria.where(ReflectionsUtil.convert(T::getBzId)).is(bzId);
        Query query = new Query(new Criteria().andOperator(criteria, baseCriteria));
        return getMongoTemplate().findOne(query, entityClass);
    }

    @Override
    public T findById(ID id) {
        Criteria baseCriteria = commonBaseCriteria();
        Criteria criteria = Criteria.where(ReflectionsUtil.convert(T::getId)).is(id);
        Query query = new Query(new Criteria().andOperator(criteria, baseCriteria));
        return getMongoTemplate().findOne(query, entityClass);
    }

    @Override
    public <S extends T> S insert(S entity) {
        setBaseFieldValue(entity, true);
        return getMongoTemplate().insert(entity);
    }

    @Override
    public <S extends T> Collection<S> insertAll(List<S> entities) {
        entities.forEach(e -> setBaseFieldValue(e, true));
        return getMongoTemplate().insertAll(entities);
    }

    @Override
    public Boolean deleteById(ID id) {
        Query query = new Query(Criteria.where(ReflectionsUtil.convert(T::getId)).is(id));
        DeleteResult result = getMongoTemplate().remove(query, entityClass);
        return result.wasAcknowledged() && result.getDeletedCount() > 0;
    }

    @Override
    public Boolean deleteByBzId(String bzId) {
        Query query = new Query(Criteria.where(ReflectionsUtil.convert(T::getBzId)).is(bzId));
        DeleteResult result = getMongoTemplate().remove(query, entityClass);
        return result.wasAcknowledged() && result.getDeletedCount() > 0;
    }

    @Override
    public Boolean delete(Criteria criteria) {
        DeleteResult result = getMongoTemplate().remove(new Query(criteria), entityClass);
        return result.wasAcknowledged() && result.getDeletedCount() > 0;
    }

    @Override
    public Boolean updateWithoutNoneById(T tObject) {
        setBaseFieldValue(tObject, false);
        Update update = new Update();
        Criteria criteria = commonBaseCriteria();
        AtomicBoolean setQueryField = new AtomicBoolean(false);
        ReflectionUtils.doWithFields(tObject.getClass(), field -> {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            if (field.get(tObject) != null && (field.isAnnotationPresent(Id.class) || "id".equals(field.getName()))) {
                criteria.and(field.getName()).is(field.get(tObject));
                setQueryField.set(true);
            }
            if ("id".equals(field.getName())                            // 排除 id 字段，因为作为查询主键
                    || field.getAnnotation(Id.class) != null            // 排除 @Id 注解字段，因为作为查询主键
                    || field.getAnnotation(Transient.class) != null     // 排除 @Transient 注解字段，因为非存储字段
                    || field.getAnnotation(EntityLogicId.class) != null // 排除 逻辑主键 字段
                    || Modifier.isStatic(field.getModifiers())) {       // 排除 静态字段
                return;
            }
            if (field.get(tObject) == null) {
                return;
            }
            update.set(field.getName(), field.get(tObject));
        });
        if (update.getUpdateObject().isEmpty()) {
            return null;
        }
        if (!setQueryField.get()) {
            return null;
        }
        UpdateResult result = getMongoTemplate().updateMulti(new Query(criteria), update, tObject.getClass());
        return result.wasAcknowledged() && result.getModifiedCount() > 0;
    }

    private Class<T> getEntityClass() {
        Type[] genericInterfaces = getClass().getGenericInterfaces();
        Type genericInterface = genericInterfaces[genericInterfaces.length - 1];
        Type[] interfaces = ((Class) genericInterface).getGenericInterfaces();
        Type[] arguments = ((ParameterizedTypeImpl) interfaces[0]).getActualTypeArguments();
        return (Class<T>) arguments[0];
    }

    private Criteria commonBaseCriteria() {
        return Criteria.where(ReflectionsUtil.convert(BaseEntity::getDelete)).is(false)
                .and(ReflectionsUtil.convert(BaseEntity::getEnabled)).is(true);
    }

    private Page<T> findBasePage(Criteria criteria, Pageable pageable, Sort sort) {
        Criteria baseCriteria = commonBaseCriteria();
        Query query = new Query(new Criteria().andOperator(criteria, baseCriteria));
        long totalRecordCnt = getMongoTemplate().count(query, entityClass);
        query.with(pageable);
        if (sort != null) {
            query.with(sort);
        }
        List<T> list = getMongoTemplate().find(query, entityClass);
        return PageableExecutionUtils.getPage(list, pageable, () -> totalRecordCnt);
    }

    private void setBaseFieldValue(T tObject, Boolean isInsert) {
        ReflectionUtils.doWithFields(tObject.getClass(), field -> {
            field.setAccessible(true);
            if (isInsert) {
                EntityLogicId entityLogicId = field.getAnnotation(EntityLogicId.class);
                if (entityLogicId != null && StringUtils.isEmpty(field.get(tObject))) {
                    // 设置bzId,UUID格式
                    field.set(tObject, UUID.randomUUID().toString().replace("-", ""));
                }
            }
        });
        long nowTime = System.currentTimeMillis();
        tObject.setGmtModified(nowTime);
        if (isInsert) {
            tObject.setGmtCreate(nowTime);
            tObject.setDelete(false);
            tObject.setEnabled(true);
        }
    }
}
