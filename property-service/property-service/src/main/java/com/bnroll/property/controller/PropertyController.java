package com.bnroll.property.controller;

import com.bnroll.property.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController("/property")
@RequiredArgsConstructor
public class PropertyController {

    @GetMapping("/my")
    public String test(
            Authentication authentication
    ) {

        UserPrincipal user =
                (UserPrincipal) authentication.getPrincipal();


        return """
                User ID: %s
                Email: %s
                Phone: %s
                Role: %s
                """.formatted(
                user.id(),
                user.email(),
                user.phone(),
                user.role()
        );
    }

    @GetMapping("/debug-db")
    public Map<String, String> debugDb(DataSource dataSource) throws Exception {

        try (Connection con = dataSource.getConnection()) {

            Map<String, String> result = new HashMap<>();

            result.put("database", con.getCatalog());
            result.put("url", con.getMetaData().getURL());
            result.put("username", con.getMetaData().getUserName());
            result.put("driver", con.getMetaData().getDriverName());

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
