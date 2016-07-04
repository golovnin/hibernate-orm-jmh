/*
 * Copyright (c) 2016, Andrej Golovnin. All rights reserved.
 *
 * JMH tests for Hibernate ORM
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package org.hibernate.service.internal;

import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.selector.spi.StrategySelector;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.batch.spi.BatchBuilder;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.engine.query.spi.NativeQueryInterpreter;
import org.hibernate.engine.spi.CacheImplementor;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatformResolver;
import org.hibernate.envers.boot.internal.EnversService;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.hibernate.integrator.spi.IntegratorService;
import org.hibernate.jmx.spi.JmxService;
import org.hibernate.persister.spi.PersisterClassResolver;
import org.hibernate.persister.spi.PersisterFactory;
import org.hibernate.property.access.spi.PropertyAccessStrategyResolver;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.search.hcore.impl.SearchFactoryReference;
import org.hibernate.secure.spi.JaccService;
import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.tool.hbm2ddl.ImportSqlCommandExtractor;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Andrej Golovnin
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 3)
@Measurement(iterations = 10, time = 3)
@State(Scope.Benchmark)
public class ConcurrentServiceBindingBenchmark {

    /*
     * In the test methods we use as keys only EventListenerRegistry.class and
     * JdbcServices.class because this keys are the most used keys in our project.
     */

    /**
     * This keys are used in a real world project.
     */
    private static final Class<?>[] KEYS = {
        CfgXmlAccessService.class,
        ClassLoaderService.class,
        StrategySelector.class,
        RegionFactory.class,
        ConfigurationService.class,
        BatchBuilder.class,
        ConnectionProvider.class,
        DialectFactory.class,
        DialectResolver.class,
        JdbcEnvironment.class,
        JdbcServices.class,
        JndiService.class,
        NativeQueryInterpreter.class,
        CacheImplementor.class,
        JtaPlatform.class,
        JtaPlatformResolver.class,
        EnversService.class,
        EventListenerRegistry.class,
        QueryTranslatorFactory.class,
        MutableIdentifierGeneratorFactory.class,
        IntegratorService.class,
        JmxService.class,
        PersisterClassResolver.class,
        PersisterFactory.class,
        PropertyAccessStrategyResolver.class,
        TransactionCoordinatorBuilder.class,
        SearchFactoryReference.class,
        JaccService.class,
        SessionFactoryServiceRegistryFactory.class,
        StatisticsImplementor.class,
        ImportSqlCommandExtractor.class,
        SchemaManagementTool.class
    };

    private ConcurrentServiceBinding<Class<?>, String> baseCsb;
    private IHMConcurrentServiceBinding<Class<?>, String> ihmCsb;
    private CHMConcurrentServiceBinding<Class<?>, String> chmCsb;

    @Setup
    public void setup() {
        baseCsb = new ConcurrentServiceBinding<>();
        ihmCsb = new IHMConcurrentServiceBinding<>();
        chmCsb = new CHMConcurrentServiceBinding<>();
        for (Class<?> key : KEYS) {
            baseCsb.put(key, "value");
            ihmCsb.put(key, "value");
            chmCsb.put(key, "value");
        }
    }

    @Benchmark
    public String baseConcurrentServiceBindingGetEventListenerRegistry() {
        return baseCsb.get(EventListenerRegistry.class);
    }

    @Benchmark
    public String baseConcurrentServiceBindingGetJdbcServices() {
        return baseCsb.get(JdbcServices.class);
    }

    @Benchmark
    public String ihmConcurrentServiceBindingGetEventListenerRegistry() {
        return ihmCsb.get(EventListenerRegistry.class);
    }

    @Benchmark
    public String ihmConcurrentServiceBindingGetJdbcServices() {
        return ihmCsb.get(JdbcServices.class);
    }

    @Benchmark
    public String chmConcurrentServiceBindingGetEventListenerRegistry() {
        return chmCsb.get(EventListenerRegistry.class);
    }

    @Benchmark
    public String chmConcurrentServiceBindingGetJdbcServices() {
        return chmCsb.get(JdbcServices.class);
    }

    public static final class IHMConcurrentServiceBinding<K,V> {

        private volatile Map<K, V> map = Collections.emptyMap();

        public synchronized void clear() {
            map = Collections.emptyMap();
        }

        public synchronized void put(final K key, final V value) {
            Map<K, V> tmp = new IdentityHashMap<>(map);
            tmp.put(key, value);
            this.map = tmp;
        }

        public V get(final K key) {
            return map.get(key);
        }

        public Iterable<V> values() {
            return map.values();
        }

    }

    public static final class CHMConcurrentServiceBinding<K,V> {

        private final Map<K, V> map = new ConcurrentHashMap<>();

        public void clear() {
            map.clear();
        }

        public void put(final K key, final V value) {
            map.put(key, value);
        }

        public V get(final K key) {
            return map.get(key);
        }

        public Iterable<V> values() {
            return map.values();
        }

    }

}
