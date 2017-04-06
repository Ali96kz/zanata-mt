package org.zanata.mt.dao;

import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.zanata.mt.JPATest;
import org.zanata.mt.api.dto.LocaleId;
import org.zanata.mt.model.Document;
import org.zanata.mt.model.Locale;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Alex Eng<a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@RunWith(CdiRunner.class)
public class DocumentDAOTest extends JPATest {

    private DocumentDAO dao;

    private Locale fromLocale = new Locale(LocaleId.EN_US, "English US");
    private Locale toLocale = new Locale(LocaleId.DE, "German");
    private Locale toLocale2 = new Locale(LocaleId.FR, "French");

    @Before
    public void setup() {
        dao = new DocumentDAO(getEm());
    }

    @Test
    public void testEmptyConstructor() {
        DocumentDAO dao = new DocumentDAO();
    }

    @Test
    public void testGetByUrlNoMatchUrl() {
        Document doc = dao.getByUrl("http://localhost3", fromLocale, toLocale);
        assertThat(doc).isNull();
    }

    @Test
    public void testGetByUrl() {
        Document doc = dao.getByUrl("http://localhost", fromLocale, toLocale);
        assertThat(doc).isNotNull();

        doc = dao.getByUrl("http://localhost2", fromLocale, toLocale);
        assertThat(doc).isNotNull();
    }

    @Test
    public void testGetListByUrl() {
        List<Document> docs =
                dao.getByUrl("http://localhost", Optional.empty(),
                        Optional.empty());
        assertThat(docs).hasSize(3);
    }

    @Test
    public void testGetListByUrlWithSourceLocale() {
        List<Document> docs =
                dao.getByUrl("http://localhost", Optional.of(fromLocale.getLocaleId()),
                        Optional.empty());
        assertThat(docs).hasSize(2);
    }

    @Test
    public void testGetListByUrlWithSourceAndTargetLocale() {
        List<Document> docs =
                dao.getByUrl("http://localhost", Optional.of(fromLocale.getLocaleId()),
                        Optional.of(toLocale.getLocaleId()));
        assertThat(docs).hasSize(1);
    }

    @Test
    public void testGetOrCreateByUrl() {
        Document doc = dao.getOrCreateByUrl("http://localhost3", fromLocale,
                toLocale);
        assertThat(doc).isNotNull();
    }

    @Test
    public void testGetUrlList() throws Exception {
        List<String> urls = dao.getUrlList();
        assertThat(urls).hasSize(2);
    }

    @Test
    public void testOnPersist() {
        Document document = new Document("http://localhost3", fromLocale,
                toLocale);
        Date creationDate = document.getCreationDate();
        Date lastChanged = document.getLastChanged();

        document = dao.persist(document);
        assertThat(document.getCreationDate()).isNotNull()
                .isNotEqualTo(creationDate);
        assertThat(document.getLastChanged()).isNotNull()
                .isNotEqualTo(lastChanged);
    }

    @Test
    public void testPreUpdate() {
        Document doc = dao.getByUrl("http://localhost", fromLocale, toLocale);
        Date lastChanged = doc.getLastChanged();
        doc.incrementUsedCount();
        dao.persist(doc);
        dao.flush();
        doc = dao.getByUrl("http://localhost", fromLocale, toLocale);
        assertThat(doc.getLastChanged()).isNotNull()
                .isNotEqualTo(lastChanged);
    }

    @Override
    protected void setupTestData() {
        getEm().persist(fromLocale);
        getEm().persist(toLocale);
        getEm().persist(toLocale2);

        Document document = new Document("http://localhost", fromLocale,
                toLocale);
        Document document2 = new Document("http://localhost", fromLocale,
                toLocale2);
        Document document3 = new Document("http://localhost", toLocale2,
                fromLocale);
        Document document4 = new Document("http://localhost2", fromLocale,
                toLocale);
        getEm().persist(document);
        getEm().persist(document2);
        getEm().persist(document3);
        getEm().persist(document4);
    }
}
