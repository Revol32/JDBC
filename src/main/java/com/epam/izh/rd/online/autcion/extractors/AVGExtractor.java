package com.epam.izh.rd.online.autcion.extractors;

import com.epam.izh.rd.online.autcion.entity.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AVGExtractor implements ResultSetExtractor<Map<User, Double>> {
    @Override
    public Map<User, Double> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<User, Double> avgItemPrise = new HashMap<>();
        while (resultSet.next()) {
            User user = new User(resultSet.getLong("user_id"),
                    resultSet.getString("billing_address"),
                    resultSet.getString("full_name"),
                    resultSet.getString("login"),
                    resultSet.getString("password"));
            avgItemPrise.put(user, resultSet.getDouble("AVG(start_price)"));
        }
        return avgItemPrise;
    }
}
