package CglibRegisterToSpring.register;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import CglibRegisterToSpring.annotation.DataSourceComponent;
import CglibRegisterToSpring.annotation.DataSourceComponentScan;
import CglibRegisterToSpring.enums.DatasourceEnum;
import CglibRegisterToSpring.filter.HsfTypeFilter;
import CglibRegisterToSpring.service.impl2.TestImpl4;
import CglibRegisterToSpring.util.InterfaceFactoryBean;
import lombok.extern.slf4j.Slf4j;

/**
 *DataSourceComponment 注册器
 */
@Slf4j
public class BeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	
    private static final String RESOURCE_PATTERN = "**/*.class";
    
    //生成的Bean名称到代理的Service Class的映射
    private static final ConcurrentHashMap<String, Class<?>> HSF_UNDERLYING_MAPPING = new ConcurrentHashMap<String, Class<?>>();

    /**
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    	//拿到主类上的自定义注解的属性
    	AnnotationAttributes annAttr = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(DataSourceComponentScan.class.getName()));
    
    	String[] basePackages = annAttr.getStringArray("value");
    	
    	if (ObjectUtils.isEmpty(basePackages)) {
            basePackages = annAttr.getStringArray("basePackages");
        }
        
        if (ObjectUtils.isEmpty(basePackages)) {
            basePackages = getPackagesFromClasses(annAttr.getClassArray("basePackageClasses"));
        }
        
        if (ObjectUtils.isEmpty(basePackages)) {
            basePackages = new String[] {ClassUtils.getPackageName(importingClassMetadata.getClassName())};
        }
        List<TypeFilter> includeFilters = extractTypeFilters(annAttr.getAnnotationArray("includeFilters"));
       
        //增加一个包含的过滤器,扫描到的类只要不是抽象的,接口,枚举,注解,及匿名类那么就算是符合的
        includeFilters.add(new HsfTypeFilter());
       
        List<TypeFilter> excludeFilters = extractTypeFilters(annAttr.getAnnotationArray("excludeFilters"));
       
        List<Class<?>> candidates = scanPackages(basePackages, includeFilters, excludeFilters);
       
        if (candidates.isEmpty()) {
            log.info("扫描指定HSF基础包[{}]时未发现复合条件的基础类", basePackages.toString());
            return;
        }
        //注册处理器后,为 对象注入环境配置信息
        //通过该类对对象进行进一步操作
        //registerHsfBeanPostProcessor(registry);
        
        //注册HSF6
       
        registerBeanDefinitions(candidates, registry);
    }

    /**
     * @param basePackages
     * @param includeFilters
     * @param excludeFilters
     * @return
     */
    private List<Class<?>> scanPackages(String[] basePackages, List<TypeFilter> includeFilters, List<TypeFilter> excludeFilters) {
        List<Class<?>> candidates = new ArrayList<Class<?>>();
        for (String pkg : basePackages) {
            try {
                candidates.addAll(findCandidateClasses(pkg, includeFilters, excludeFilters));
            } catch (IOException e) {
                log.error("扫描指定HSF基础包[{}]时出现异常", pkg);
                continue;
            }
        }
        return candidates;
    }

    /**
     * @param basePackage
     * @return
     * @throws IOException
     */
    private List<Class<?>> findCandidateClasses(String basePackage, List<TypeFilter> includeFilters, List<TypeFilter> excludeFilters) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("开始扫描指定包{}下的所有类" + basePackage);
        }
        List<Class<?>> candidates = new ArrayList<Class<?>>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + replaceDotByDelimiter(basePackage) + '/' + RESOURCE_PATTERN;
        
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        MetadataReaderFactory readerFactory = new SimpleMetadataReaderFactory(resourceLoader);
        Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(packageSearchPath);
        for (Resource resource : resources) {
            MetadataReader reader = readerFactory.getMetadataReader(resource);
            if (isCandidateResource(reader, readerFactory, includeFilters, excludeFilters)) {
                Class<?> candidateClass = transform(reader.getClassMetadata().getClassName());

                if (candidateClass != null) {
                    candidates.add(candidateClass);
                    log.debug("扫描到符合要求基础类:{}" + candidateClass.getName());
                }
            }
        }
        return candidates;
    }

    /**
     * 注册 Bean,
     * Bean的名称格式:
     * @param internalClasses
     * @param registry
     */
    private void registerBeanDefinitions(List<Class<?>> internalClasses, BeanDefinitionRegistry registry) {
        for (Class<?> clazz : internalClasses) {
            if (HSF_UNDERLYING_MAPPING.values().contains(clazz)) {
                log.debug("重复扫描{}类,忽略重复注册", clazz.getName());
                continue;
            }
            
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                GenericBeanDefinition definition = (GenericBeanDefinition) builder.getRawBeanDefinition();
               
                definition.getPropertyValues().add("interfaceClass", clazz);
               
                Enum value = clazz.getAnnotation(DataSourceComponent.class).DataSource();
                
                definition.getPropertyValues().add("value",value);
                definition.setBeanClass(InterfaceFactoryBean.class);
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            if (registerSpringBean(clazz)) {
                log.debug("注册[{}]Bean", clazz.getName());
                registry.registerBeanDefinition(ClassUtils.getShortNameAsProperty(clazz), definition);
            }
            HSF_UNDERLYING_MAPPING.put(ClassUtils.getShortNameAsProperty(clazz), clazz);
        }
    }
    
    // 首字母转小写
	  private static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	  }
    
 
    /**
     * @param filterAttributes
     * @return
     */
    private List<TypeFilter> typeFiltersFor(AnnotationAttributes filterAttributes) {
        List<TypeFilter> typeFilters = new ArrayList<TypeFilter>();
        FilterType filterType = filterAttributes.getEnum("type");

        for (Class<?> filterClass : filterAttributes.getClassArray("classes")) {
            switch (filterType) {
                case ANNOTATION:
                    Assert.isAssignable(Annotation.class, filterClass,
                        "@HsfComponentScan 注解类型的Filter必须指定一个注解");
                    Class<Annotation> annotationType = (Class<Annotation>)filterClass;
                    typeFilters.add(new AnnotationTypeFilter(annotationType));
                    break;
                case ASSIGNABLE_TYPE:
                    typeFilters.add(new AssignableTypeFilter(filterClass));
                    break;
                case CUSTOM:
                    Assert.isAssignable(TypeFilter.class, filterClass,
                        "@HsfComponentScan 自定义Filter必须实现TypeFilter接口");
                    TypeFilter filter = BeanUtils.instantiateClass(filterClass, TypeFilter.class);
                    typeFilters.add(filter);
                    break;
                default:
                    throw new IllegalArgumentException("当前TypeFilter不支持: " + filterType);
            }
        }
        return typeFilters;
    }

    /**
     * @param classes
     * @return
     */
    private String[] getPackagesFromClasses(Class[] classes) {
        if (ObjectUtils.isEmpty(classes)) {
            return null;
        }
        List<String> basePackages = new ArrayList<String>(classes.length);
        for (Class<?> clazz : classes) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        return (String[])basePackages.toArray();
    }

    /**
     * 用"/"替换包路径中"."
     *
     * @param path
     * @return
     */
    private String replaceDotByDelimiter(String path) {
        return StringUtils.replace(path, ".", "/");
    }

    /**
     * @param reader
     * @param readerFactory
     * @param includeFilters
     * @param excludeFilters
     * @return
     * @throws IOException
     */
    protected boolean isCandidateResource(MetadataReader reader, MetadataReaderFactory readerFactory, List<TypeFilter> includeFilters,
                                          List<TypeFilter> excludeFilters) throws IOException {
        for (TypeFilter tf : excludeFilters) {
            if (tf.match(reader, readerFactory)) {
                return false;
            }
        }
        for (TypeFilter tf : includeFilters) {
            if (tf.match(reader, readerFactory)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param className
     * @return
     */
    private Class<?> transform(String className) {
        Class<?> clazz = null;
        try {
            clazz = ClassUtils.forName(className, this.getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            log.info("未找到指定类{%s}", className);
        }
        return clazz;
    }

    /**
     * @param annAttrs
     * @return
     */
    private List<TypeFilter> extractTypeFilters(AnnotationAttributes[] annAttrs) {
        List<TypeFilter> typeFilters = new ArrayList<TypeFilter>();
        for (AnnotationAttributes filter : annAttrs) {
            typeFilters.addAll(typeFiltersFor(filter));
        }
        return typeFilters;
    }

    /**
     * @param beanClass
     * @return
     */
    private boolean registerSpringBean(Class<?> beanClass) {
        return beanClass.getAnnotation(DataSourceComponent.class).registerBean();
    }

    /**
     * @param hsfBeanName
     * @return
     */
    public static Class<?> getUnderlyingClass(String hsfBeanName) {
        return HSF_UNDERLYING_MAPPING.get(hsfBeanName);
    }
}
