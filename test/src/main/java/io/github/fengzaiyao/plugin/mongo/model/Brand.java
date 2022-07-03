package io.github.fengzaiyao.plugin.mongo.model;

import io.github.fengzaiyao.plugin.mongo.core.model.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@Document("brand")
public class Brand extends BaseEntity {

    private String name;

    private Integer number;
}