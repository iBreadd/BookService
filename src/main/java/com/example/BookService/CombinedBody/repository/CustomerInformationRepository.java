package com.example.BookService.CombinedBody.repository;

import com.example.BookService.CombinedBody.entity.CustomerInformation;
import com.example.BookService.CombinedBody.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerInformationRepository extends JpaRepository<CustomerInformation, String> {
    @Query("""
            SELECT ci FROM CustomerInformation ci
            WHERE ci.customerId= :customerId
            AND ci.applicationId= :applicationId
            AND ci.requestType= :requestType
            order by ci.offsetValue asc
            """)
    List<CustomerInformation> findCustomerInformationByIdCustomerIdApplicationIdRequestType(String customerId, String applicationId, RequestType requestType);
}
