package io.github.fengzaiyao.plugin.mongo.test.service;

import io.github.fengzaiyao.plugin.mongo.core.service.BaseServiceImpl;
import io.github.fengzaiyao.plugin.mongo.dynamic.annotation.DataSource;
import io.github.fengzaiyao.plugin.mongo.test.model.Brand;
import org.springframework.stereotype.Service;

@Service
@DataSource("datasource1")
public class BrandServiceImpl extends BaseServiceImpl<Brand, String> implements IBrandService {
}