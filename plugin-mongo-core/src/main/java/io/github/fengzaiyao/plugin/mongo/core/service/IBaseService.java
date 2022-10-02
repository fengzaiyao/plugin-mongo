package io.github.fengzaiyao.plugin.mongo.core.service;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import io.github.fengzaiyao.plugin.mongo.core.model.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Collection;
import java.util.List;

public interface IBaseService<T extends BaseEntity, ID> {

    MongoTemplate getMongoTemplate();

    void setMongoTemplate(MongoTemplate mongoTemplate);

    Page<T> findPage(Criteria criteria, Pageable pageable, Sort sort);

    Page<T> findPage(Criteria criteria, Pageable pageable);

    List<T> findAllByBzId(Collection<String> bzIds);

    List<T> findAllById(Collection<ID> ids);

    List<T> find(Criteria criteria);

    T findByBzId(String bzId);

    T findById(ID id);

    <S extends T> S insert(S entity);

    <S extends T> Collection<S> insertAll(Collection<S> entities);

    DeleteResult deleteById(ID id);

    DeleteResult deleteByBzId(String bzId);

    DeleteResult delete(Criteria criteria);

    UpdateResult updateWithoutNoneById(T tObject);
}