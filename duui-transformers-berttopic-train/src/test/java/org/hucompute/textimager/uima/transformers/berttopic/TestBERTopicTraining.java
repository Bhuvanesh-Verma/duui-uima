package org.hucompute.textimager.uima.transformers.berttopic;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XmlCasSerializer;
import org.junit.jupiter.api.*;
import org.texttechnologylab.DockerUnifiedUIMAInterface.DUUIComposer;
import org.texttechnologylab.DockerUnifiedUIMAInterface.driver.DUUIRemoteDriver;
import org.texttechnologylab.DockerUnifiedUIMAInterface.io.DUUIAsynchronousProcessor;
import org.texttechnologylab.DockerUnifiedUIMAInterface.io.reader.DUUIFileReaderLazy;
import org.texttechnologylab.DockerUnifiedUIMAInterface.lua.DUUILuaContext;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class TestBERTopicTraining {

    static DUUIComposer composer;
    static String url = "http://127.0.0.1:8000";
    static JCas cas;


    @BeforeAll
    static void beforeAll() throws URISyntaxException, IOException {
        composer = (new DUUIComposer())
                .withSkipVerification(true)
                .withLuaContext((new DUUILuaContext()).withJsonLibrary());
        DUUIRemoteDriver remoteDriver = new DUUIRemoteDriver();
        composer.addDriver(remoteDriver);
    }

    @AfterAll
    static void afterAll() throws UnknownHostException {
        composer.shutdown();
    }

    @Test
    public void testTrainBERTopic() throws Exception {

        DUUIFileReaderLazy reader = new DUUIFileReaderLazy(
                "/Users/bhuvanesh/Documents/code/corpora/wiki_sample/input", ".xmi.gz.xmi");
        DUUIAsynchronousProcessor processor =
                new DUUIAsynchronousProcessor(reader);

        composer.add(new DUUIRemoteDriver.Component(url)
                .withParameter("selection", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence")
                .build()
                        .withTimeout(1000L)
                .withTrainable(true));

        composer.run(processor, "berttopic-training");

        composer.shutdown();
    }
}