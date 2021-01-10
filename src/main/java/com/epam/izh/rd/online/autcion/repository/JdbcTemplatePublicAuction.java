package com.epam.izh.rd.online.autcion.repository;

import com.epam.izh.rd.online.autcion.entity.Bid;
import com.epam.izh.rd.online.autcion.entity.Item;
import com.epam.izh.rd.online.autcion.entity.User;
import com.epam.izh.rd.online.autcion.extractors.AVGExtractor;
import com.epam.izh.rd.online.autcion.extractors.ItemBidExtractor;
import com.epam.izh.rd.online.autcion.mappers.BidMapper;
import com.epam.izh.rd.online.autcion.mappers.ItemMapper;
import com.epam.izh.rd.online.autcion.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component
public class JdbcTemplatePublicAuction implements PublicAuction {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BidMapper bidMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AVGExtractor avgExtractor;
    @Autowired
    private ItemBidExtractor itemBidExtractor;

    @Override
    public List<Bid> getUserBids(long id) {
        String query = "SELECT * FROM `bids` WHERE `user_id`= ?";
        return jdbcTemplate.query(query, bidMapper, id);
    }

    @Override
    public List<Item> getUserItems(long id) {
        String query = "SELECT * FROM `items` WHERE `user_id`= ?";
        return jdbcTemplate.query(query, itemMapper, id);
    }

    @Override
    public Item getItemByName(String name) {
        String query = "SELECT * FROM `items` WHERE `title` LIKE '%" + name + "%'";
        List<Item> items = jdbcTemplate.query(query, itemMapper);
        return items.get(0);
    }

    @Override
    public Item getItemByDescription(String name) {
        String query = "SELECT * FROM `items` WHERE `description` LIKE '%" + name + "%'";
        List<Item> items = jdbcTemplate.query(query, itemMapper);
        return items.get(0);
    }

    @Override
    public Map<User, Double> getAvgItemCost() {
        String query = "SELECT users.*,AVG(start_prise) FROM `items` INNER JOIN `users` ON items.user_id=users.user_id GROUP BY users.user_id";
        return jdbcTemplate.query(query, avgExtractor);
    }

    @Override
    public Map<Item, Bid> getMaxBidsForEveryItem() {
        String query = "SELECT `bid_id`,`bid_date`,MAX(bid_value),bids.item_id,bids.user_id,items.* FROM `bids` INNER JOIN items ON bids.item_id=items.item_id GROUP BY item_id";
        return jdbcTemplate.query(query,itemBidExtractor);
    }

    @Override
    public boolean createUser(User user) {
        return false;
    }

    @Override
    public boolean createItem(Item item) {
        return false;
    }

    @Override
    public boolean createBid(Bid bid) {
        return false;
    }

    @Override
    public boolean deleteUserBids(long id) {
        return false;
    }

    @Override
    public boolean doubleItemsStartPrice(long id) {
        return false;
    }
}
