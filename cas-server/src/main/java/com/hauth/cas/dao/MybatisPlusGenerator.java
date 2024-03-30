package com.hauth.cas.dao;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2024/3/8 11:20
 */
public class MybatisPlusGenerator {

    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/cas_server?useSSL=false";
        String user = "root";
        String pass = "root";
        String module = "cas-server";
        FastAutoGenerator.create(url, user, pass)
                .globalConfig(builder -> {
                    builder.author("hundanli")// 设置作者
                            .outputDir(System.getProperty("user.dir") + "/" + module + "/src/main/java"); // 指定输出目录
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent("com.hauth.cas.dao"); // 设置父包名
//                            .moduleName("auth-server") // 设置父包模块名
//                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/" + module + "/src/main/java")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("cas_account", "cas_client", "cas_consent"); // 设置需要生成的表名
                })
                .templateEngine(new FreemarkerTemplateEngine())
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}