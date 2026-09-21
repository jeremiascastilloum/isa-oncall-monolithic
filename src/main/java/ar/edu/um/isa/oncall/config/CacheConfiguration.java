package ar.edu.um.isa.oncall.config;

import java.time.Duration;
import org.ehcache.config.builders.*;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.cache.autoconfigure.JCacheManagerCustomizer;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        var ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                Object.class,
                Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries())
            )
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build()
        );
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, ar.edu.um.isa.oncall.repository.UserRepository.USERS_BY_LOGIN_CACHE);
            createCache(cm, ar.edu.um.isa.oncall.repository.UserRepository.USERS_BY_EMAIL_CACHE);
            createCache(cm, ar.edu.um.isa.oncall.domain.User.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Authority.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.User.class.getName() + ".authorities");
            createCache(cm, ar.edu.um.isa.oncall.domain.Equipo.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Equipo.class.getName() + ".servicios");
            createCache(cm, ar.edu.um.isa.oncall.domain.Equipo.class.getName() + ".rotacions");
            createCache(cm, ar.edu.um.isa.oncall.domain.Servicio.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Servicio.class.getName() + ".objetivos");
            createCache(cm, ar.edu.um.isa.oncall.domain.Servicio.class.getName() + ".alertas");
            createCache(cm, ar.edu.um.isa.oncall.domain.Servicio.class.getName() + ".politicas");
            createCache(cm, ar.edu.um.isa.oncall.domain.Servicio.class.getName() + ".incidentes");
            createCache(cm, ar.edu.um.isa.oncall.domain.ObjetivoDeServicio.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Alerta.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Incidente.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Incidente.class.getName() + ".servicios");
            createCache(cm, ar.edu.um.isa.oncall.domain.Incidente.class.getName() + ".alertas");
            createCache(cm, ar.edu.um.isa.oncall.domain.Incidente.class.getName() + ".eventos");
            createCache(cm, ar.edu.um.isa.oncall.domain.Incidente.class.getName() + ".notificacions");
            createCache(cm, ar.edu.um.isa.oncall.domain.EventoDeIncidente.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Rotacion.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Rotacion.class.getName() + ".turnos");
            createCache(cm, ar.edu.um.isa.oncall.domain.Rotacion.class.getName() + ".pasos");
            createCache(cm, ar.edu.um.isa.oncall.domain.TurnoDeGuardia.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.PoliticaEscalamiento.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.PoliticaEscalamiento.class.getName() + ".pasos");
            createCache(cm, ar.edu.um.isa.oncall.domain.PasoEscalamiento.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Notificacion.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Postmortem.class.getName());
            createCache(cm, ar.edu.um.isa.oncall.domain.Postmortem.class.getName() + ".accions");
            createCache(cm, ar.edu.um.isa.oncall.domain.AccionCorrectiva.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }
}
