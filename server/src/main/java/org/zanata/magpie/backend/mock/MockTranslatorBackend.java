package org.zanata.magpie.backend.mock;

import org.zanata.magpie.api.dto.LocaleCode;
import org.zanata.magpie.backend.BackendLocaleCode;
import org.zanata.magpie.exception.MTException;
import org.zanata.magpie.model.AugmentedTranslation;
import org.zanata.magpie.model.BackendID;
import org.zanata.magpie.service.TranslatorBackend;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mock backend service for when DEV mode is enabled.
 * See {@link org.zanata.magpie.model.BackendID#DEV}
 *
 * This service will return translations of original string with prefix of
 * {@link #PREFIX_MOCK_STRING}.
 *
 * See {@link org.zanata.magpie.service.ConfigurationService#isDevMode}
 *
 * @author Alex Eng <a href="mailto:aeng@redhat.com">aeng@redhat.com</a>
 */
@ApplicationScoped
public class MockTranslatorBackend implements TranslatorBackend {

    // Max length per request for mock service
    private final static int MAX_LENGTH = 10000;

    public final static String PREFIX_MOCK_STRING = "translated[";
    public final static String UNICODE_SUPPLEMENTARY = "\uD843\uDFB4";

    @SuppressWarnings("unused")
    public MockTranslatorBackend() {
    }

    @Override
    public List<AugmentedTranslation> translate(List<String> contents,
            BackendLocaleCode srcLocale, BackendLocaleCode targetLocale,
            MediaType mediaType, Optional<String> category)
            throws MTException {
        List<AugmentedTranslation> translations = contents.stream()
                .map(source -> new AugmentedTranslation(
                        wrapContentInBlock(source),
                        wrapContentInBlock(source)))
                .collect(Collectors.toList());
        return translations;
    }

    /**
     * Return same localeCode as parsed in
     */
    @Override
    public BackendLocaleCode getMappedLocale(LocaleCode localeCode) {
        return new MockLocaleCode(localeCode);
    }

    @Override
    public int getCharLimitPerRequest() {
        return MAX_LENGTH;
    }

    @Override
    public BackendID getId() {
        return BackendID.DEV;
    }

    public static class MockLocaleCode implements BackendLocaleCode {
        private String localeCode;

        public MockLocaleCode(@NotNull LocaleCode localeCode) {
            this.localeCode = localeCode.getId();
        }

        @Override
        public String getLocaleCode() {
            return localeCode;
        }
    }

    private String wrapContentInBlock(String content) {
        return PREFIX_MOCK_STRING + UNICODE_SUPPLEMENTARY + " " + content +
                " " + UNICODE_SUPPLEMENTARY + "]";
    }
}
