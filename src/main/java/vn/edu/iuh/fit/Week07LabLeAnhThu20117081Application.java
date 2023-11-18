package vn.edu.iuh.fit;

import net.datafaker.Faker;
import net.datafaker.providers.base.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.iuh.fit.backend.enums.ProductStatus;
import vn.edu.iuh.fit.backend.models.Product;
import vn.edu.iuh.fit.backend.models.ProductImage;
import vn.edu.iuh.fit.backend.models.ProductPrice;
import vn.edu.iuh.fit.backend.repositories.ProductPriceRepository;
import vn.edu.iuh.fit.backend.repositories.ProductRepository;

import java.util.Random;


@SpringBootApplication
public class Week07LabLeAnhThu20117081Application {

    public static void main(String[] args) {
        SpringApplication.run(Week07LabLeAnhThu20117081Application.class, args);
    }
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductPriceRepository productPriceRepository;
    @Bean
    @Transactional
    CommandLineRunner createSampleProducts(){
        return args -> {
            Random red= new Random();
            Faker faker=new Faker();
            Device device=faker.device();

            for (int i=0;i<20;i++){
                Product product=new Product(device.modelName(),faker.lorem().paragraph(10),"unit",device.manufacturer(), ProductStatus.ACTIVE);
                productRepository.save(product);

                for(int j=0;j<3;j++){
                    ProductPrice productPrice=new ProductPrice(faker.date().birthday().toLocalDateTime(), red.nextInt(1000000), faker.lorem().paragraph(2));
                    productPrice.setProduct(product);
                    productPriceRepository.save(productPrice);
                }
            }
        };
    }
}
