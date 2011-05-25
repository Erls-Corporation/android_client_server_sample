package org.devproof.mubble.androidclient;

import android.app.ProgressDialog;
import com.google.inject.TypeLiteral;
import com.j256.ormlite.dao.Dao;
import org.apache.commons.lang.UnhandledException;
import org.devproof.mubble.androidclient.orm.DbHelper;
import org.devproof.mubble.androidclient.provider.ProgressDialogProvider;
import org.devproof.mubble.dto.message.DisplayGeoMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import roboguice.config.AbstractAndroidModule;
import roboguice.inject.SharedPreferencesName;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Configures guice IOC
 *
 * @author Carsten Hufe
 */
public class MubbleModule extends AbstractAndroidModule {
    private DbHelper dbHelper;

    public MubbleModule(MubbleApplication mubbleApplication) {
        this.dbHelper = createDbHelper(mubbleApplication);
    }

    @Override
    protected void configure() {
        // set my preferences to default
        bindConstant().annotatedWith(SharedPreferencesName.class).to(getClass().getPackage().getName() + "_preferences");
        bind(ProgressDialog.class).toProvider(ProgressDialogProvider.class);
        // configure my main DAO with ormlite and add it
        Dao<DisplayGeoMessage, String> geoMessageDao = createDao(DisplayGeoMessage.class);
        bind(new TypeLiteral<Dao<DisplayGeoMessage, String>>() {}).toInstance(geoMessageDao);
        // configure the default RestTemplate with Jackson
        bind(RestTemplate.class).toInstance(createDefaultRestTemplate());
    }

    private <T> T createDao(Class<?> clazz) {
        try {
            @SuppressWarnings({"unchecked"})
            T dao = (T)dbHelper.getDao(clazz);
            return dao;
        } catch (SQLException e) {
            throw new UnhandledException(e);
        }
    }

    private DbHelper createDbHelper(MubbleApplication mubbleApplication) {
        DbHelper dbHelper = new DbHelper(mubbleApplication, MubbleConstants.DB_NAME, MubbleConstants.DB_VERSION, DisplayGeoMessage.class);
        dbHelper.getWritableDatabase().close();
        return dbHelper;
    }

    private RestTemplate createDefaultRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new MappingJacksonHttpMessageConverter());
		restTemplate.setMessageConverters(converters);
        return restTemplate;
    }
}
