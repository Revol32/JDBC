package com.epam.izh.rd.online.autcion.extractors;

import com.epam.izh.rd.online.autcion.entity.Bid;
import com.epam.izh.rd.online.autcion.entity.Item;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ItemBidExtractor implements ResultSetExtractor<Map<Item, Bid>> {
    @Override
    public Map<Item, Bid> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Map<Item, Bid> itemBidMap = new HashMap<>();
        while (resultSet.next()) {
            Item item = new Item(resultSet.getLong("item_id"),
                    resultSet.getDouble("bid_increment"),
                    resultSet.getBoolean("buy_it_now"),
                    resultSet.getString("description"),
                    resultSet.getDate("start_date").toLocalDate(),
                    resultSet.getDouble("start_price"),
                    resultSet.getDate("stop_date").toLocalDate(),
                    resultSet.getString("title"),
                    resultSet.getLong("user_id"));
            Bid bid = new Bid(resultSet.getLong("bid_id"),
                    resultSet.getDate("bid_date").toLocalDate(),
                    resultSet.getDouble("MAX(bid_value)"),
                    resultSet.getLong("item_id"),
                    resultSet.getLong("user_id"));
            itemBidMap.put(item, bid);
        }
        return itemBidMap;
    }
}
