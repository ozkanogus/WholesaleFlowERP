package tr.com.erpsample.grocery.initializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import tr.com.erpsample.grocery.repository.GroceryRepository;
import tr.com.erpsample.grocery.repository.ProductRepository;
import tr.com.erpsample.grocery.service.PurchaseService;
import tr.com.erpsample.grocery.service.SaleService;
import tr.com.erpsample.grocery.service.mapper.GroceryMapper;
import tr.com.erpsample.grocery.service.mapper.ProductMapper;

class DataPopulatorConfigurationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
        .withUserConfiguration(DataPopulator.class);

    @Test
    void demoInitializerIsAbsentByDefault() {
        runner.run(context -> assertThat(context).hasNotFailed().doesNotHaveBean(DataPopulator.class));
    }

    @Test
    void explicitFalseKeepsDemoInitializerAbsent() {
        runner.withPropertyValues("grocery.demo-data.enabled=false")
            .run(context -> assertThat(context).hasNotFailed().doesNotHaveBean(DataPopulator.class));
    }

    @Test
    void explicitTrueRegistersDemoInitializer() {
        runner.withPropertyValues("grocery.demo-data.enabled=true")
            .withBean(GroceryMapper.class, () -> mock(GroceryMapper.class))
            .withBean(ProductMapper.class, () -> mock(ProductMapper.class))
            .withBean(GroceryRepository.class, () -> mock(GroceryRepository.class))
            .withBean(ProductRepository.class, () -> mock(ProductRepository.class))
            .withBean(PurchaseService.class, () -> mock(PurchaseService.class))
            .withBean(SaleService.class, () -> mock(SaleService.class))
            .run(context -> assertThat(context).hasNotFailed().hasSingleBean(DataPopulator.class));
    }
}
