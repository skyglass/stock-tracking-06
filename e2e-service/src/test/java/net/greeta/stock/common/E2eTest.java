package net.greeta.stock.common;

import lombok.SneakyThrows;
import net.greeta.stock.config.MockHelper;
import net.greeta.stock.customerpayment.CustomerPaymentTestDataService;
import net.greeta.stock.inventory.InventoryTestDataService;
import net.greeta.stock.order.OrderTestDataService;
import net.greeta.stock.shipping.ShippingTestDataService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public abstract class E2eTest {

    @Value("${security.oauth2.username}")
    private String securityOauth2Username;

    @Value("${security.oauth2.password}")
    private String securityOauth2Password;

    @Autowired
    private MockHelper mockHelper;

    @Autowired
    private CustomerPaymentTestDataService customerPaymentTestDataService;

    @Autowired
    private InventoryTestDataService inventoryTestDataService;

    @Autowired
    private OrderTestDataService orderTestDataService;

    @Autowired
    private ShippingTestDataService shippingTestDataService;

    @BeforeEach
    @SneakyThrows
    void cleanup() {
        mockHelper.mockCredentials(securityOauth2Username, securityOauth2Password);
        orderTestDataService.resetDatabase();
        inventoryTestDataService.resetDatabase();
        shippingTestDataService.resetDatabase();
        customerPaymentTestDataService.resetDatabase();
        //TimeUnit.MILLISECONDS.sleep(Duration.ofSeconds(1).toMillis());
    }
}
