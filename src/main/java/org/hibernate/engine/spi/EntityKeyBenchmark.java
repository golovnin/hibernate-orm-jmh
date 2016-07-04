/*
 * Copyright (c) 2016, Andrej Golovnin. All rights reserved.
 *
 * JMH tests for Hibernate ORM
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package org.hibernate.engine.spi;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.cache.spi.entry.CacheEntryStructure;
import org.hibernate.engine.jdbc.Size;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.FilterAliasGenerator;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.MultiLoadOptions;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.hibernate.persister.walking.spi.EntityIdentifierDefinition;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.Type;
import org.hibernate.type.VersionType;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;

/**
 * @author Andrej Golovnin
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 3)
@Measurement(iterations = 10, time = 3)
public class EntityKeyBenchmark {

    private static final Type IDENTIFIER_TYPE = new MockIdentifierType();
    private static final EntityPersister PERSISTER = new MockEntityPersister();
    private static final Long IDENTIFIER = 100000L;

    @Benchmark
    public EntityKey createEntityKey() {
        return new EntityKey(IDENTIFIER, PERSISTER);
    }

    private static final class MockIdentifierType implements Type {
        @Override
        public boolean isAssociationType() {
            return false;
        }

        @Override
        public boolean isCollectionType() {
            return false;
        }

        @Override
        public boolean isEntityType() {
            return false;
        }

        @Override
        public boolean isAnyType() {
            return false;
        }

        @Override
        public boolean isComponentType() {
            return false;
        }

        @Override
        public int getColumnSpan(Mapping mapping) throws MappingException {
            return 0;
        }

        @Override
        public int[] sqlTypes(Mapping mapping) throws MappingException {
            return new int[0];
        }

        @Override
        public Size[] dictatedSizes(Mapping mapping) throws MappingException {
            return new Size[0];
        }

        @Override
        public Size[] defaultSizes(Mapping mapping) throws MappingException {
            return new Size[0];
        }

        @Override
        public Class getReturnedClass() {
            return null;
        }

        @Override
        public boolean isSame(Object x, Object y) throws HibernateException {
            return false;
        }

        @Override
        public boolean isEqual(Object x, Object y) throws HibernateException {
            return false;
        }

        @Override
        public boolean isEqual(Object x, Object y,
            SessionFactoryImplementor factory) throws HibernateException
        {
            return false;
        }

        @Override
        public int getHashCode(Object x) throws HibernateException {
            return 0;
        }

        @Override
        public int getHashCode(Object x, SessionFactoryImplementor factory)
            throws HibernateException
        {
            return x.hashCode();
        }

        @Override
        public int compare(Object x, Object y) {
            return 0;
        }

        @Override
        public boolean isDirty(Object old, Object current,
            SharedSessionContractImplementor session) throws HibernateException
        {
            return false;
        }

        @Override
        public boolean isDirty(Object oldState, Object currentState,
            boolean[] checkable, SharedSessionContractImplementor session)
            throws HibernateException
        {
            return false;
        }

        @Override
        public boolean isModified(Object dbState, Object currentState,
            boolean[] checkable, SharedSessionContractImplementor session)
            throws HibernateException
        {
            return false;
        }

        @Override
        public Object nullSafeGet(ResultSet rs, String[] names,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException
        {
            return null;
        }

        @Override
        public Object nullSafeGet(ResultSet rs, String name,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException
        {
            return null;
        }

        @Override
        public void nullSafeSet(PreparedStatement st, Object value, int index,
            boolean[] settable, SharedSessionContractImplementor session)
            throws HibernateException, SQLException
        {

        }

        @Override
        public void nullSafeSet(PreparedStatement st, Object value, int index,
            SharedSessionContractImplementor session)
            throws HibernateException, SQLException
        {

        }

        @Override
        public String toLoggableString(Object value,
            SessionFactoryImplementor factory) throws HibernateException
        {
            return null;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Object deepCopy(Object value, SessionFactoryImplementor factory)
            throws HibernateException
        {
            return null;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public Serializable disassemble(Object value,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException
        {
            return null;
        }

        @Override
        public Object assemble(Serializable cached,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException
        {
            return null;
        }

        @Override
        public void beforeAssemble(Serializable cached,
            SharedSessionContractImplementor session)
        {

        }

        @Override
        public Object hydrate(ResultSet rs, String[] names,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException
        {
            return null;
        }

        @Override
        public Object resolve(Object value,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException
        {
            return null;
        }

        @Override
        public Object semiResolve(Object value,
            SharedSessionContractImplementor session, Object owner)
            throws HibernateException
        {
            return null;
        }

        @Override
        public Type getSemiResolvedType(SessionFactoryImplementor factory) {
            return null;
        }

        @Override
        public Object replace(Object original, Object target,
            SharedSessionContractImplementor session, Object owner,
            Map copyCache) throws HibernateException
        {
            return null;
        }

        @Override
        public Object replace(Object original, Object target,
            SharedSessionContractImplementor session, Object owner,
            Map copyCache, ForeignKeyDirection foreignKeyDirection)
            throws HibernateException
        {
            return null;
        }

        @Override
        public boolean[] toColumnNullness(Object value, Mapping mapping) {
            return new boolean[0];
        }
    }

    private static final class MockEntityPersister implements EntityPersister {

        @Override
        public void generateEntityDefinition() {
            // NOP
        }

        @Override
        public void postInstantiate() throws MappingException {
            // NOP
        }

        @Override
        public SessionFactoryImplementor getFactory() {
            return null;
        }

        @Override
        public EntityEntryFactory getEntityEntryFactory() {
            return null;
        }

        @Override
        public String getRootEntityName() {
            return "ROOT_ENTITY_NAME";
        }

        @Override
        public String getEntityName() {
            return null;
        }

        @Override
        public EntityMetamodel getEntityMetamodel() {
            return null;
        }

        @Override
        public boolean isSubclassEntityName(String entityName) {
            return false;
        }

        @Override
        public Serializable[] getPropertySpaces() {
            return new Serializable[0];
        }

        @Override
        public Serializable[] getQuerySpaces() {
            return new Serializable[0];
        }

        @Override
        public boolean hasProxy() {
            return false;
        }

        @Override
        public boolean hasCollections() {
            return false;
        }

        @Override
        public boolean hasMutableProperties() {
            return false;
        }

        @Override
        public boolean hasSubselectLoadableCollections() {
            return false;
        }

        @Override
        public boolean hasCascades() {
            return false;
        }

        @Override
        public boolean isMutable() {
            return false;
        }

        @Override
        public boolean isInherited() {
            return false;
        }

        @Override
        public boolean isIdentifierAssignedByInsert() {
            return false;
        }

        @Override
        public Type getPropertyType(String propertyName)
            throws MappingException
        {
            return null;
        }

        @Override
        public int[] findDirty(Object[] currentState, Object[] previousState,
            Object owner, SharedSessionContractImplementor session)
        {
            return new int[0];
        }

        @Override
        public int[] findModified(Object[] old, Object[] current, Object object,
            SharedSessionContractImplementor session)
        {
            return new int[0];
        }

        @Override
        public boolean hasIdentifierProperty() {
            return false;
        }

        @Override
        public boolean canExtractIdOutOfEntity() {
            return false;
        }

        @Override
        public boolean isVersioned() {
            return false;
        }

        @Override
        public VersionType getVersionType() {
            return null;
        }

        @Override
        public int getVersionProperty() {
            return 0;
        }

        @Override
        public boolean hasNaturalIdentifier() {
            return false;
        }

        @Override
        public int[] getNaturalIdentifierProperties() {
            return new int[0];
        }

        @Override
        public Object[] getNaturalIdentifierSnapshot(Serializable id,
            SharedSessionContractImplementor session)
        {
            return new Object[0];
        }

        @Override
        public IdentifierGenerator getIdentifierGenerator() {
            return null;
        }

        @Override
        public boolean hasLazyProperties() {
            return false;
        }

        @Override
        public Serializable loadEntityIdByNaturalId(Object[] naturalIdValues,
            LockOptions lockOptions, SharedSessionContractImplementor session)
        {
            return null;
        }

        @Override
        public Object load(Serializable id, Object optionalObject,
            LockMode lockMode, SharedSessionContractImplementor session)
            throws HibernateException
        {
            return null;
        }

        @Override
        public Object load(Serializable id, Object optionalObject,
            LockOptions lockOptions, SharedSessionContractImplementor session)
            throws HibernateException
        {
            return null;
        }

        @Override
        public List multiLoad(Serializable[] ids,
            SharedSessionContractImplementor session,
            MultiLoadOptions loadOptions)
        {
            return null;
        }

        @Override
        public void lock(Serializable id, Object version, Object object,
            LockMode lockMode, SharedSessionContractImplementor session)
            throws HibernateException
        {

        }

        @Override
        public void lock(Serializable id, Object version, Object object,
            LockOptions lockOptions, SharedSessionContractImplementor session)
            throws HibernateException
        {

        }

        @Override
        public void insert(Serializable id, Object[] fields, Object object,
            SharedSessionContractImplementor session) throws HibernateException
        {

        }

        @Override
        public Serializable insert(Object[] fields, Object object,
            SharedSessionContractImplementor session) throws HibernateException
        {
            return null;
        }

        @Override
        public void delete(Serializable id, Object version, Object object,
            SharedSessionContractImplementor session) throws HibernateException
        {

        }

        @Override
        public void update(Serializable id, Object[] fields, int[] dirtyFields,
            boolean hasDirtyCollection, Object[] oldFields, Object oldVersion,
            Object object, Object rowId,
            SharedSessionContractImplementor session) throws HibernateException
        {

        }

        @Override
        public Type[] getPropertyTypes() {
            return new Type[0];
        }

        @Override
        public String[] getPropertyNames() {
            return new String[0];
        }

        @Override
        public boolean[] getPropertyInsertability() {
            return new boolean[0];
        }

        @Override
        public ValueInclusion[] getPropertyInsertGenerationInclusions() {
            return new ValueInclusion[0];
        }

        @Override
        public ValueInclusion[] getPropertyUpdateGenerationInclusions() {
            return new ValueInclusion[0];
        }

        @Override
        public boolean[] getPropertyUpdateability() {
            return new boolean[0];
        }

        @Override
        public boolean[] getPropertyCheckability() {
            return new boolean[0];
        }

        @Override
        public boolean[] getPropertyNullability() {
            return new boolean[0];
        }

        @Override
        public boolean[] getPropertyVersionability() {
            return new boolean[0];
        }

        @Override
        public boolean[] getPropertyLaziness() {
            return new boolean[0];
        }

        @Override
        public CascadeStyle[] getPropertyCascadeStyles() {
            return new CascadeStyle[0];
        }

        @Override
        public Type getIdentifierType() {
            return IDENTIFIER_TYPE;
        }

        @Override
        public String getIdentifierPropertyName() {
            return null;
        }

        @Override
        public boolean isCacheInvalidationRequired() {
            return false;
        }

        @Override
        public boolean isLazyPropertiesCacheable() {
            return false;
        }

        @Override
        public boolean hasCache() {
            return false;
        }

        @Override
        public EntityRegionAccessStrategy getCacheAccessStrategy() {
            return null;
        }

        @Override
        public CacheEntryStructure getCacheEntryStructure() {
            return null;
        }

        @Override
        public CacheEntry buildCacheEntry(Object entity, Object[] state,
            Object version, SharedSessionContractImplementor session)
        {
            return null;
        }

        @Override
        public boolean hasNaturalIdCache() {
            return false;
        }

        @Override
        public NaturalIdRegionAccessStrategy getNaturalIdCacheAccessStrategy() {
            return null;
        }

        @Override
        public ClassMetadata getClassMetadata() {
            return null;
        }

        @Override
        public boolean isBatchLoadable() {
            return false;
        }

        @Override
        public boolean isSelectBeforeUpdateRequired() {
            return false;
        }

        @Override
        public Object[] getDatabaseSnapshot(Serializable id,
            SharedSessionContractImplementor session) throws HibernateException
        {
            return new Object[0];
        }

        @Override
        public Serializable getIdByUniqueKey(Serializable key,
            String uniquePropertyName, SharedSessionContractImplementor session)
        {
            return null;
        }

        @Override
        public Object getCurrentVersion(Serializable id,
            SharedSessionContractImplementor session) throws HibernateException
        {
            return null;
        }

        @Override
        public Object forceVersionIncrement(Serializable id,
            Object currentVersion, SharedSessionContractImplementor session)
            throws HibernateException
        {
            return null;
        }

        @Override
        public boolean isInstrumented() {
            return false;
        }

        @Override
        public boolean hasInsertGeneratedProperties() {
            return false;
        }

        @Override
        public boolean hasUpdateGeneratedProperties() {
            return false;
        }

        @Override
        public boolean isVersionPropertyGenerated() {
            return false;
        }

        @Override
        public void afterInitialize(Object entity,
            SharedSessionContractImplementor session)
        {
            // NOP
        }

        @Override
        public void afterReassociate(Object entity,
            SharedSessionContractImplementor session)
        {
            // NOP
        }

        @Override
        public Object createProxy(Serializable id,
            SharedSessionContractImplementor session) throws HibernateException
        {
            return null;
        }

        @Override
        public Boolean isTransient(Object object,
            SharedSessionContractImplementor session) throws HibernateException
        {
            return null;
        }

        @Override
        public Object[] getPropertyValuesToInsert(Object object, Map mergeMap,
            SharedSessionContractImplementor session) throws HibernateException
        {
            return new Object[0];
        }

        @Override
        public void processInsertGeneratedProperties(Serializable id,
            Object entity, Object[] state,
            SharedSessionContractImplementor session)
        {
            // NOP
        }

        @Override
        public void processUpdateGeneratedProperties(Serializable id,
            Object entity, Object[] state,
            SharedSessionContractImplementor session)
        {
            // NOP
        }

        @Override
        public Class getMappedClass() {
            return null;
        }

        @Override
        public boolean implementsLifecycle() {
            return false;
        }

        @Override
        public Class getConcreteProxyClass() {
            return null;
        }

        @Override
        public void setPropertyValues(Object object, Object[] values) {
            // NOP
        }

        @Override
        public void setPropertyValue(Object object, int i, Object value) {
            // NOP
        }

        @Override
        public Object[] getPropertyValues(Object object) {
            return new Object[0];
        }

        @Override
        public Object getPropertyValue(Object object, int i)
            throws HibernateException
        {
            return null;
        }

        @Override
        public Object getPropertyValue(Object object, String propertyName) {
            return null;
        }

        @Override
        public Serializable getIdentifier(Object object)
            throws HibernateException
        {
            return null;
        }

        @Override
        public Serializable getIdentifier(Object entity,
            SharedSessionContractImplementor session)
        {
            return null;
        }

        @Override
        public void setIdentifier(Object entity, Serializable id,
            SharedSessionContractImplementor session)
        {
            // NOP
        }

        @Override
        public Object getVersion(Object object) throws HibernateException {
            return null;
        }

        @Override
        public Object instantiate(Serializable id,
            SharedSessionContractImplementor session)
        {
            return null;
        }

        @Override
        public boolean isInstance(Object object) {
            return false;
        }

        @Override
        public boolean hasUninitializedLazyProperties(Object object) {
            return false;
        }

        @Override
        public void resetIdentifier(Object entity, Serializable currentId,
            Object currentVersion, SharedSessionContractImplementor session)
        {
            // NOP
        }

        @Override
        public EntityPersister getSubclassEntityPersister(Object instance,
            SessionFactoryImplementor factory)
        {
            return null;
        }

        @Override
        public EntityMode getEntityMode() {
            return null;
        }

        @Override
        public EntityTuplizer getEntityTuplizer() {
            return null;
        }

        @Override
        public BytecodeEnhancementMetadata getInstrumentationMetadata() {
            return null;
        }

        @Override
        public FilterAliasGenerator getFilterAliasGenerator(String rootAlias) {
            return null;
        }

        @Override
        public int[] resolveAttributeIndexes(String[] attributeNames) {
            return new int[0];
        }

        @Override
        public boolean canUseReferenceCacheEntries() {
            return false;
        }

        @Override
        public Comparator getVersionComparator() {
            return null;
        }

        @Override
        public EntityPersister getEntityPersister() {
            return null;
        }

        @Override
        public EntityIdentifierDefinition getEntityKeyDefinition() {
            return null;
        }

        @Override
        public Iterable<AttributeDefinition> getAttributes() {
            return null;
        }
    }

}
