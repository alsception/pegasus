package org.alsception.pegasus.features.bootboard.repositories;

import org.alsception.pegasus.features.bootboard.utils.UniqueIdGenerator;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.alsception.pegasus.features.bootboard.entities.BBCard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@Repository
public class BBCardRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private static final String TABLE_NAME = "bb_cards";
    private static final String SELECT_CLAUSE = "SELECT * FROM "+TABLE_NAME+"";
    private static final String WHERE_ID = " WHERE id = ?";
    private static final String WHERE_PARENT_ID = " WHERE list_id = ?";
    /*private static final String ORDER_BY = " ORDER BY CASE WHEN position > 0 THEN 0 ELSE 1 END ASC, position ASC, updated ASC, id ASC";*/
    private static final String ORDER_BY = " ORDER BY position ASC, id ASC ";
    
    public BBCardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }   

    public BBCard create(BBCard card) throws Exception
    {
        card.setId(UniqueIdGenerator.generateNanoId());

        String sql = "INSERT INTO " + TABLE_NAME + " (user_id, list_id, title, description, color, type, position, id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        System.out.println(sql);

        // TODO - remove this KeyHolder and RETURN_GENERATED_KEYS because we will generate id in java
        
        // Using KeyHolder to capture the generated key        
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, card.getUserId());
            ps.setLong(2, card.getListId());
            ps.setString(3, card.getTitle());
            ps.setString(4, card.getDescription());
            ps.setString(5, card.getColor());
            ps.setString(6, card.getType());
            ps.setInt(7, card.getPosition());
            ps.setLong(8, card.getId().longValue());
            return ps;
        }, keyHolder);
        
        // Fetch the complete object from the database
        return findById(card.getId()).orElseThrow(() -> new Exception("Error creating card. Could not load new card from database ERR59"));
    }
    
    public List<BBCard> findAll() {
        String sql = SELECT_CLAUSE+ORDER_BY;
        return jdbcTemplate.query(sql, (rs, rowNum) ->                                
            new BBCard(
                rs.getLong("id"),                         
                rs.getLong("user_id"),                         
                rs.getLong("list_id"),                         
                rs.getString("title"),    
                rs.getString("description"), 
                rs.getString("color"),
                rs.getString("type"), 
                rs.getInt("position"),
                rs.getTimestamp("created").toLocalDateTime(),
                rs.getTimestamp("updated").toLocalDateTime()
            ));
    }
    
    public Optional<BBCard> findById(Long id) {
        String sql = SELECT_CLAUSE + WHERE_ID;
        try {
            BBCard card = jdbcTemplate.queryForObject(sql, new Object[]{id}, cardRowMapper());
            return Optional.of(card); // Wrap the result in Optional
        } catch (EmptyResultDataAccessException e) {
            System.out.println("No card found with id: " + id);
            return Optional.empty(); // Return an empty Optional if no result is found
        }
    }
    
    public Optional<List<BBCard>> findByListId(Long listId) 
    {
        String sql = SELECT_CLAUSE + " WHERE list_id = ? "+ORDER_BY;
        
        try {
            List<BBCard> cards = jdbcTemplate.query(sql, new Object[]{listId}, // Pass the parameter value for the placeholder
                    (rs, rowNum) -> new BBCard(
                        rs.getLong("id"),
                        rs.getLong("user_id"),
                        rs.getLong("list_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("color"),
                        rs.getString("type"),
                        rs.getInt("position"),
                        rs.getTimestamp("created").toLocalDateTime(),
                        rs.getTimestamp("updated").toLocalDateTime()
                    ));
            return Optional.of(cards); // Wrap the result in Optional
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty(); // Return an empty Optional if no result is found
        }
    }
    
    // RowMapper to map result set to BBCard object
    private RowMapper<BBCard> cardRowMapper() {
        return (rs, rowNum) -> {
            BBCard card = new BBCard();
            card.setId(rs.getLong("id"));
            card.setListId(rs.getLong("list_id"));
            card.setUserId(rs.getLong("user_id"));
            card.setTitle(rs.getString("title"));
            card.setDescription(rs.getString("description"));
            card.setColor(rs.getString("color"));
            card.setType(rs.getString("type"));
            card.setPosition(rs.getInt("position"));
            card.setCreated(rs.getTimestamp("created").toLocalDateTime());
            card.setUpdated(rs.getTimestamp("updated").toLocalDateTime());
            return card;
        };
    }
    
    public int update(BBCard e) {
        String sql = "UPDATE " + TABLE_NAME + " SET "
                + "user_id = ?, "
                + "list_id = ?, "
                + "description = ?, "
                + "title = ?, "
                + "color = ?, "
                + "type = ?, "
                + "position = ?, "
                + "updated = CURRENT_TIMESTAMP "
                + WHERE_ID;    
        return jdbcTemplate.update(sql, e.getUserId(), e.getListId(), e.getDescription(), e.getTitle(), e.getColor(), e.getType(), e.getPosition(), e.getId());
    }
    
    public int delete(Long id) {
        String sql = "DELETE FROM " + TABLE_NAME + WHERE_ID;
        return jdbcTemplate.update(sql, id);
    }
    
    //This function deletes child cards for parent board
public int deleteForList(Long cardId) 
    {          
        String sql = "DELETE FROM " + TABLE_NAME + WHERE_PARENT_ID;
        return jdbcTemplate.update(sql, cardId);
    }
    
    public boolean swapPositions(long id1, long id2)
    {
        // dev-Comment: 
        // This logic should(?) actually go in some other (business) layer between controller and repository
        // But for now ok
        
        //1. Load 1 card
        //2. load 2 card
        //3. swap
        //4. save
        
        if(id1==id2){
            System.out.println("INFO: Received same ids for swapping: "+id1);
            return true;
        }
            
        BBCard card1 = findById(id1).orElseThrow(() -> new RuntimeException("Card with id not found: "+id1));        
        BBCard card2 = findById(id2).orElseThrow(() -> new RuntimeException("Card with id not found: "+id2));
        
        int position1 = card1.getPosition();
        int position2 = card2.getPosition();
        
        if(position1 == position2){
            System.out.println("WARN: Both positions are the same: "+position1);
            return true;
        }
        
        card1.setPosition(position2);
        card2.setPosition(position1);
        
        int result = 0;
        
        result += update(card1);
        result += update(card2);
        
        return result == 2;
    }
    
}
