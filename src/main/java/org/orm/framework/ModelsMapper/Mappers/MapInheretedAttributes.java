package org.orm.framework.ModelsMapper.Mappers;

import org.orm.framework.EntitiesDataSource.EntitiesDataSource;
import org.orm.framework.EntitiesDataSource.Entity;
import org.orm.framework.ModelsMapper.FieldsMapper.Attribute.Attribute;
import org.orm.framework.ModelsMapper.FieldsMapper.Attribute.AttributeList;
import org.orm.framework.ModelsMapper.FieldsMapper.PrimaryKey.PrimaryKey;

import java.util.List;
import java.util.stream.Collectors;

public class MapInheretedAttributes {
    public static void mapInheritance(Entity entity) {
        mapPrimaryKey(entity, entity.getSupperClass());

        mapInheritanceNormalAttr(entity, entity.getSupperClass());

        mapInheritanceRelation(entity, entity.getSupperClass());
    }

    public static void mapPrimaryKey(Entity entity, Class<?> supperClass) {
        Entity superClassEntity = EntitiesDataSource
                .getModelsSchemas()
                .get(supperClass.getSimpleName());

        PrimaryKey inheritedPrimaryKey = superClassEntity
                .getPrimaryKey();

        if (superClassEntity.getSupperClass() != Object.class)
            mapPrimaryKey(entity, superClassEntity.getSupperClass());
        else
            entity.setPrimaryKey(inheritedPrimaryKey);
    }
    public static void mapInheritanceRelation(Entity entity, Class<?> supperClass) {
        Entity superClassEntity = EntitiesDataSource.getModelsSchemas().get(supperClass.getSimpleName());

        List<Attribute> inheritedAttrs = superClassEntity
                .getRelationalAtrributes()
                .stream()
                .filter(Attribute::isInherited)
                .collect(Collectors.toList());
        inheritedAttrs.forEach(a -> a.addHeritant(entity.getModel()));

        if (superClassEntity.getSupperClass() != Object.class)
            mapInheritanceRelation(entity, superClassEntity.getSupperClass());
    }

    public static void mapInheritanceNormalAttr(Entity entity, Class<?> supperClass) {
        Entity superClassEntity = EntitiesDataSource.getModelsSchemas().get(supperClass.getSimpleName());

        List<Attribute> inheritedAttrs = superClassEntity
                .getNormalAttributes()
                .stream()
                .filter(Attribute::isInherited)
                .collect(Collectors.toList());

        inheritedAttrs.forEach(a -> {
            Attribute clone;
            if (a instanceof AttributeList)
                clone = new AttributeList((AttributeList) a);
            else
                clone = new Attribute(a);

            clone.setName(a.getName());
            clone.setOriginalName(a.getOriginalName());
            clone.setClazz(entity.getModel());

            entity.addNormalInheritedAttr(clone);
        });

        if (superClassEntity.getSupperClass() != Object.class)
            mapInheritanceNormalAttr(entity,superClassEntity.getSupperClass());
    }

}
