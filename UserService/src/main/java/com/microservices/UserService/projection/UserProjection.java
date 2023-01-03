package com.microservices.UserService.projection;

import com.microservices.CommonService.entity.CardDetails;
import com.microservices.CommonService.entity.User;
import com.microservices.CommonService.queries.GetUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserProjection {

    @QueryHandler
    public User getUserPaymentDetails(GetUserPaymentDetailsQuery getUserPaymentDetailsQuery) {
        //get the details from DB

        CardDetails cardDetails =
                CardDetails.builder()
                        .name("Vladimir Velikov")
                        .cardNumber("1234567836573213")
                        .validUntilMonth(04)
                        .validUntilYear(2023)
                        .cvv(123)
                        .build();

        return User.builder()
                .userId(getUserPaymentDetailsQuery.getUserId())
                .firstName("Vladimir")
                .lastName("Velikov")
                .cardDetails(cardDetails)
                .build();
    }
}
