package com.tinet.ctilink;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import com.tinet.ctilink.model.EnterpriseSetting;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
/**
 * @author fengwei //
 * @date 16/4/11 15:52
 */

public class RestEasyClient {

    public static void main(String[] args) {

        EnterpriseSetting es = new EnterpriseSetting();
        es.setEnterpriseId(50000001);
        es.setName("test1");

        try {
            ResteasyClient client = new ResteasyClientBuilder().build();

            ResteasyWebTarget target = client
                    .target("http://localhost:8089/v1/enterpriseSetting/get");

            Response response = target.request().post(
                    Entity.entity(es, "application/json"));

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            System.out.println("Server response : \n");
            System.out.println(response.readEntity(String.class));

            response.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}