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
        String query = "SELECT users.*,AVG(start_price) FROM `items` INNER JOIN `users` ON items.user_id=users.user_id GROUP BY users.user_id";
        return jdbcTemplate.query(query, avgExtractor);
    }

    @Override
    public Map<Item, Bid> getMaxBidsForEveryItem() {
        String query = "SELECT items.*, x.* FROM items INNER JOIN " +
                "(SELECT bids.`bid_id`,bids.bid_date,bids.bid_value, bids.user_id as biduser_id, y.* FROM bids INNER JOIN " +
                "(SELECT MAX(bid_value) as maxbid,bids.item_id FROM `bids`GROUP BY bids.item_id) AS y " +
                "ON bids.item_id = y.item_id AND bids.bid_value=y.maxbid GROUP BY bids.item_id) AS x " +
                "ON items.item_id=x.item_id GROUP BY items.item_id;";
        return jdbcTemplate.query(query, itemBidExtractor);
    }

    @Override
    public boolean createUser(User user) {

        return jdbcTemplate.update(
                "INSERT INTO `users` (`user_id`, `full_name`, `billing_address`, `login`, `password`) VALUES (?,?,?,?,?)",
                user.getUserId(), user.getFullName(), user.getBillingAddress(), user.getLogin(), user.getPassword()) != 0;
    }

    @Override
    public boolean createItem(Item item) {
        return jdbcTemplate.update(
                "INSERT INTO `items` (`item_id`, `title`, `description`, `start_price`, `bid_increment`, `start_date`, `stop_date`, `buy_it_now`, `user_id`)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                item.getItemId(), item.getTitle(), item.getDescription(), item.getStartPrice(), item.getBidIncrement(), item.getStartDate(),
                item.getStopDate(), item.getBuyItNow(), item.getUserId()) != 0;
    }

    @Override
    public boolean createBid(Bid bid) {
        return jdbcTemplate.update(
                "INSERT INTO `bids` (`bid_id`, `bid_date`, `bid_value`, `item_id`, `user_id`) VALUES (?, ?, ?, ?, ?)",
                bid.getBidId(), bid.getBidDate(), bid.getBidValue(), bid.getItemId(), bid.getUserId()) != 0;
    }

    @Override
    public boolean deleteUserBids(long id) {
        return jdbcTemplate.update("DELETE FROM `bids` WHERE `user_id`=?", id) != 0;
    }

    @Override
    public boolean doubleItemsStartPrice(long id) {
        return jdbcTemplate.update("UPDATE `items` SET `start_price` = `start_price`*2 WHERE `user_id`=?", id) != 0;
    }
}
