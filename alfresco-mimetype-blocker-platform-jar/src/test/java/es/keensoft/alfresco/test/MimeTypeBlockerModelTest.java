package es.keensoft.alfresco.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.alfresco.repo.dictionary.CompiledModelsCache;
import org.alfresco.repo.dictionary.DictionaryBootstrap;
import org.alfresco.repo.dictionary.DictionaryDAOImpl;
import org.alfresco.repo.tenant.SingleTServiceImpl;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.util.DynamicallySizedThreadPoolExecutor;
import org.alfresco.util.TraceableThreadFactory;
import org.alfresco.util.cache.DefaultAsynchronouslyRefreshedCacheRegistry;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ContentModelTest.
 */
public class MimeTypeBlockerModelTest {
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(MimeTypeBlockerModelTest.class);

    /**
     * Test bootstrap model.
     *
     * @throws Exception the exception
     */
    @Test
    public void testBootstrapModel() throws Exception {
		testBootstrapModel(new String[] {
				"alfresco/module/alfresco-mimetype-blocker-platform-jar/model/mimetypeBlockerModel.xml"});
    }
    
    /**
	 * Test bootstrap model.
	 *
	 * @param models the models
	 * @throws Exception the exception
	 */
    private static void testBootstrapModel(final String[] models) throws Exception {
		final List<String> bootstrapModels = new ArrayList<String>();
		//BlogIntegration model is no longer available in Version 5.2.6
		bootstrapModels.add("alfresco/model/dictionaryModel.xml");
		bootstrapModels.add("alfresco/model/systemModel.xml");
		bootstrapModels.add("org/alfresco/repo/security/authentication/userModel.xml");
		bootstrapModels.add("alfresco/model/contentModel.xml");		
		bootstrapModels.add("alfresco/model/applicationModel.xml");
		bootstrapModels.add("alfresco/model/bpmModel.xml");
		bootstrapModels.add("alfresco/model/calendarModel.xml");
		bootstrapModels.add("alfresco/model/cmisTestModel.xml");
		bootstrapModels.add("alfresco/model/customModelManagementModel.xml");
		bootstrapModels.add("alfresco/model/defaultCustomModel.xml");
		bootstrapModels.add("alfresco/model/distributionPoliciesModel.xml");
		bootstrapModels.add("alfresco/model/downloadModel.xml");
		bootstrapModels.add("alfresco/model/emailServerModel.xml");
		bootstrapModels.add("alfresco/model/forumModel.xml");
		bootstrapModels.add("alfresco/model/imapModel.xml");
		bootstrapModels.add("alfresco/model/linksModel.xml");
		bootstrapModels.add("alfresco/model/quickShareModel.xml");
		bootstrapModels.add("alfresco/model/remoteCredentialsModel.xml");
		bootstrapModels.add("alfresco/model/siteModel.xml");
		bootstrapModels.add("alfresco/model/smartfolder-model.xml");
		bootstrapModels.add("alfresco/model/solrFacetModel.xml");
		bootstrapModels.add("alfresco/model/surfModel.xml");
		bootstrapModels.add("alfresco/model/transferModel.xml");
		bootstrapModels.add("alfresco/model/datalistModel.xml");

		if (models != null) {
			bootstrapModels.addAll(Arrays.asList(models));
		}

		final TenantService tenantService = new SingleTServiceImpl();

		final DictionaryDAOImpl dictionaryDAO = new DictionaryDAOImpl();
		dictionaryDAO.setTenantService(tenantService);

		initDictionaryCaches(dictionaryDAO, tenantService);

		final DictionaryBootstrap bootstrap = new DictionaryBootstrap();
		try {
			bootstrap.setModels(bootstrapModels);
			bootstrap.setDictionaryDAO(dictionaryDAO);
			bootstrap.bootstrap();
		} catch (Exception excp) {
			LOGGER.error("Found an invalid model...", excp);
			Throwable throwable = excp;
			while (throwable != null) {
				LOGGER.warn(throwable.getMessage());
				throwable = throwable.getCause();
			}
			throw excp;
		}
	}

	/**
	 * Inits the dictionary caches.
	 *
	 * @param dictionaryDAO the dictionary dao
	 * @param tenantService the tenant service
	 */
	private static void initDictionaryCaches(final DictionaryDAOImpl dictionaryDAO,
			final TenantService tenantService) {
		final CompiledModelsCache compiledModelsCache = new CompiledModelsCache();
		compiledModelsCache.setDictionaryDAO(dictionaryDAO);
		compiledModelsCache.setTenantService(tenantService);
		compiledModelsCache
				.setRegistry(new DefaultAsynchronouslyRefreshedCacheRegistry());
		compiledModelsCache.setThreadPoolExecutor(getThreadPoolExecutor());
		dictionaryDAO.setDictionaryRegistryCache(compiledModelsCache);
	}

	/**
	 * Gets the thread pool executor.
	 *
	 * @return the thread pool executor
	 */
	private static DynamicallySizedThreadPoolExecutor getThreadPoolExecutor() {
		final String poolName = "Dictionary-Pool";
		final TraceableThreadFactory threadFactory = new TraceableThreadFactory();
		threadFactory.setThreadDaemon(true);
		threadFactory.setThreadPriority(5);
		threadFactory.setNamePrefix(poolName);

		final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();

		final DynamicallySizedThreadPoolExecutor threadPool = new DynamicallySizedThreadPoolExecutor(
				4, 4, 120L, TimeUnit.SECONDS, workQueue, threadFactory,
				new ThreadPoolExecutor.CallerRunsPolicy());
		return threadPool;
	}
}