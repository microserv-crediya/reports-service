package co.com.jhompo.config;

import co.com.jhompo.model.approved.Approved;
import co.com.jhompo.model.approved.gateways.ApprovedRepository;
import co.com.jhompo.usecase.approved.ApprovedUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Configuration
@ComponentScan(basePackages = "co.com.jhompo.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean
    public ApprovedUseCase approvedUseCase(ApprovedRepository repository) {
        return new ApprovedUseCase(repository);
    }

    @Bean
    public ApprovedRepository approvedRepository() {
        // Opción 1: Para testing
        return new ApprovedRepository() {
            @Override
            public Mono<Approved> getCount() {
                return Mono.empty();
            }

            @Override
            public Mono<Approved> updateReport(BigDecimal amount) {
                return Mono.empty();
            }
        };

    }
}
