package com.alipay.rdf.file.cache;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.alipay.rdf.file.common.CacheManager;
import com.alipay.rdf.file.function.ColumnFunctionWrapper;
import com.alipay.rdf.file.interfaces.FileFactory;
import com.alipay.rdf.file.interfaces.FileReader;
import com.alipay.rdf.file.loader.FormatLoader;
import com.alipay.rdf.file.loader.ProcessorLoader;
import com.alipay.rdf.file.loader.ProtocolLoader;
import com.alipay.rdf.file.loader.TemplateLoader;
import com.alipay.rdf.file.model.FileConfig;
import com.alipay.rdf.file.model.StorageConfig;
import com.alipay.rdf.file.util.DateUtil;

import junit.framework.Assert;

public class CacheManagerTest {

    @Test
    public void testRemoveDataTempalteCache() {

        String templatePath = "/cache/fund/template/data1.cfg";
        FileConfig config = new FileConfig(
            File.class.getResource("/cache/fund/data/data1.txt").getPath(), templatePath,
            new StorageConfig("nas"));

        FileReader fileReader = FileFactory.createReader(config);

        Map<String, Object> head = fileReader.readHead(HashMap.class);
        int count = (Integer) head.get("totalCount");

        Map<String, Object> row = null;
        for (int i = 0; i < count; i++) {
            row = fileReader.readRow(HashMap.class);
        }

        Assert.assertEquals("20151204",
            DateUtil.format((Date) row.get("TransactionCfmDate"), "yyyyMMdd"));
        Assert.assertEquals("中国9", row.get("FundCode"));
        Assert.assertEquals(new Integer(9), row.get("AvailableVol"));

        fileReader.readTail(HashMap.class);

        fileReader.close();

        System.out.println(ColumnFunctionWrapper.columnRegExs.keySet());
        Assert.assertNotNull(ColumnFunctionWrapper.columnRegExs
            .get("classpath:" + templatePath + "-TransactionCfmDate"));
        Assert.assertNotNull(
            ColumnFunctionWrapper.columnRegExs.get("classpath:" + templatePath + "-FundCode"));
        Assert.assertNotNull(
            ColumnFunctionWrapper.columnRegExs.get("classpath:" + templatePath + "-AvailableVol"));
        Assert.assertNotNull(
            ColumnFunctionWrapper.columnRegExs.get("classpath:" + templatePath + "-fileEnd"));
        Assert.assertEquals(13, ColumnFunctionWrapper.columnRegExs.size());

        Assert.assertNotNull(TemplateLoader.CACHE.get("classpath:" + templatePath));
        Assert.assertNotNull(TemplateLoader.ROW_LENGTH_CACHE.get(templatePath));

        CacheManager.removeDataTempalteCache(templatePath);

        Assert.assertNull(ColumnFunctionWrapper.columnRegExs
            .get("classpath:" + templatePath + "-TransactionCfmDate"));
        Assert.assertNull(
            ColumnFunctionWrapper.columnRegExs.get("classpath:" + templatePath + "-FundCode"));
        Assert.assertNull(
            ColumnFunctionWrapper.columnRegExs.get("classpath:" + templatePath + "-AvailableVol"));
        Assert.assertTrue(ColumnFunctionWrapper.columnRegExs.isEmpty());

        Assert.assertNull(TemplateLoader.CACHE.get("classpath:" + templatePath));
        Assert.assertNull(TemplateLoader.ROW_LENGTH_CACHE.get(templatePath));

        //processor
        Assert.assertNotNull(ProcessorLoader.DEFAULT_PROCESSORS);
        Assert.assertNotNull(ProtocolLoader.PROTOCOL_PROCESSOR_CACHE.get("fund"));
        Assert.assertNotNull(ProtocolLoader.PROTOCOL_PROCESSOR_CACHE.get("FUND"));

        CacheManager.removeGlobalDefaultProcessorsCache();
        Assert.assertNull(ProcessorLoader.DEFAULT_PROCESSORS);

        CacheManager.removeProtocolDefaultProcessorsCache("fund");
        Assert.assertNull(ProtocolLoader.PROTOCOL_PROCESSOR_CACHE.get("fund"));
        Assert.assertNull(ProtocolLoader.PROTOCOL_PROCESSOR_CACHE.get("FUND"));

    }

    @Test
    public void testRemoveProtocolTemplateCache() {
        ProtocolLoader.loadProtocol("fund");

        Assert.assertNotNull(ProtocolLoader.PD_CACHE.get("fund"));
        Assert.assertNotNull(ProtocolLoader.PD_CACHE.get("FUND"));

        CacheManager.removeProtocolTemplateCache("fund");

        Assert.assertNull(ProtocolLoader.PD_CACHE.get("fund"));
        Assert.assertNull(ProtocolLoader.PD_CACHE.get("FUND"));

    }

    @Test
    public void testRemoveFormatCache() {
        FormatLoader.getColumnFormt("fund", "String");
        Assert.assertNotNull(FormatLoader.TYPEFORMATHOLDER_CACHE.get("fund"));

        FormatLoader.getColumnFormt("FUND", "String");
        Assert.assertNotNull(FormatLoader.TYPEFORMATHOLDER_CACHE.get("FUND"));

        CacheManager.removeFormatCache("fund");
        Assert.assertNull(FormatLoader.TYPEFORMATHOLDER_CACHE.get("fund"));
        Assert.assertNull(FormatLoader.TYPEFORMATHOLDER_CACHE.get("FUND"));
    }
}
