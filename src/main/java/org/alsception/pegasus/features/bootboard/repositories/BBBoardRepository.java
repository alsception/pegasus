package org.alsception.pegasus.features.bootboard.repositories;

import org.alsception.pegasus.features.bootboard.utils.UniqueIdGenerator;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import org.alsception.pegasus.features.bootboard.entities.BBCard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.alsception.pegasus.features.bootboard.entities.BBBoard;
import org.alsception.pegasus.features.bootboard.entities.BBList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

@Repository
public class BBBoardRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private static final String TABLE_NAME = "bb_boards";
    private static final String SELECT_CLAUSE = "SELECT * FROM "+TABLE_NAME+ " ";
    private static final String WHERE_ID = " WHERE id = ?";
    private static final String WHERE_ID_GREATER = " WHERE id > ?";
    private static final String ORDER_BY = " ORDER BY CASE WHEN position > 0 THEN 0 ELSE 1 END ASC, position ASC, id ASC";
    private static final String ORDER_BY_INVERSE = " ORDER BY CASE WHEN position > 0 THEN 0 ELSE 1 END DESC, position DESC, id DESC";
    
    @Autowired
    private BBCardRepository cardRepository;
    
    @Autowired
    private BBListRepository listRepository;

    public BBBoardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }   
    
    public BBBoard save(BBBoard board) throws Exception
    {
        String sql = "INSERT INTO " + TABLE_NAME + " (id, user_id, title, color, type, position, created) VALUES (?, ?, ?, ?, ?, ?, ?)";
        System.out.println(sql);        
        // Using KeyHolder to capture the generated key
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, board.getId());
            ps.setLong(2, board.getUserId());
            ps.setString(3, board.getTitle());
            ps.setString(4, board.getColor().toLowerCase());
            ps.setString(5, board.getType());
            ps.setInt(6, board.getPosition());
            ps.setTimestamp(7,  new Timestamp(System.currentTimeMillis()));
            return ps;
        }, keyHolder);
        
        // Fetch the complete object from the database
        BBBoard createdBoard = findById(board.getId()).orElseThrow(() -> new Exception("Error creating board. Could not load new board from database ERR60"));

        //addCards(createdBoard);
        
        //again, with cards, if requested
        if(null!=createdBoard.getType() && createdBoard.getType().startsWith("ADD")){
            //createdBoard = findById(generatedId).orElseThrow(() -> new Exception("Error creating board. Could not load new card from database ERR66"));
        }
        
        return createdBoard;    
    }
    
    /**
     * Diff between save and create is that one assign id and other not     
     */

    public BBBoard create(BBBoard board) throws Exception
    {
        board.setId(UniqueIdGenerator.generateNanoId());
        return this.save(board);
    }

    private void addCards(BBBoard createdBoard) {
        //now if requested by client:
        //create n cards.

        String type = createdBoard.getType();
        if( null != type && !"".equals(type))
        {
            if(type.startsWith("ADD_CARDS")){
                
                stringExtractor(type);
                
                String numberPart = type.replaceAll("\\D+", ""); // Remove all non-digit characters
                try{
                    int numberParam = Integer.parseInt(numberPart);
                    
                    if(numberParam < 0 || numberParam > 999 ){
                        //-1 could be interesting....
                        throw new Exception("Invalid number of ADD_CARDS");
                    }else{
                        for(int i = 0; i < numberParam; i++)
                        {
                            //create card.
                            System.out.println("Creating card "+i);
                            BBCard card = new BBCard();
                            card.setDescription("Description");
                            card.setColor("color");
                            card.setType("type");
                            card.setPosition(0);
                            this.cardRepository.create(card);
                        }
                    }
                }catch(Exception e){
                    System.out.println("Error extracting number part. ERR71");
                }
            }
        }
    }
    
    private HashMap<String,Object> stringExtractor(String input) 
    {
        //test input would be = "ADD_CARDS_10_DESCRIPTION_ASDASDASD_COLOR_RED_TYPE_X";
        
        HashMap<String,Object> hmParameters = new HashMap<>();
        
        // Ensure string starts with "ADD"
        if (input != null && input.startsWith("ADD")) {
            // Updated regex to match space-separated parts
            String regex = "(?:CARDS (\\d+))?(?: DESCRIPTION ([^ ]+))?(?: COLOR ([^ ]+))?(?: TYPE (\\w+))?";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(input);

            if (matcher.find()) {
                // Extract the values (or set them to null if not present)
                String number = matcher.group(1);
                String description = matcher.group(2);
                String color = matcher.group(3);
                String type = matcher.group(4);
                
                hmParameters.put("number", number);
                hmParameters.put("description", description);
                hmParameters.put("color", color);
                hmParameters.put("type", type);

                // Print the extracted parts, handling nulls
                /*System.out.println("Number after 'CARDS': " + (number != null ? number : "Not present"));
                System.out.println("Description: " + (description != null ? description : "Not present"));
                System.out.println("Color: " + (color != null ? color : "Not present"));
                System.out.println("Type: " + (type != null ? type : "Not present"));*/
            } else {
                System.out.println("No match found.");
            }
        } else {
            System.out.println("String does not start with 'ADD'.");
        }
        
        return hmParameters;
    }
    
    //1. Load board without lists
    public Optional<BBBoard> findById(Long id) {
        String sql = SELECT_CLAUSE + WHERE_ID;
        try {
            BBBoard cboard = jdbcTemplate.queryForObject(sql, new Object[]{id}, cboardRowMapper());
            return Optional.of(cboard); // Wrap the result in Optional
        } catch (EmptyResultDataAccessException e) {
            System.out.println("No card board found with id: " + id);
            return Optional.empty(); // Return an empty Optional if no result is found
        }
    }
    
    public List<BBCard> findCardsForList(Long id) 
    {
        Optional<BBList> cardList = null;//this.findById(id);
        
        if(cardList.isPresent())
        {
            try{
                Optional<List<BBCard>> cards = this.cardRepository.findByListId(id);
                if(cards.isPresent())
                {
                    return cards.get();
                }
            }catch(Exception e){
                System.out.println("Error loading cards for list "+id);
                e.printStackTrace();
                return null;
            }            
        }
        return null;
    }    
    
    public Optional<BBBoard> findByIdWithLists(Long id) 
    {
        Optional<BBBoard> board = this.findById(id);
        
        if(board.isPresent())
        {
            try{
                Optional<List<BBList>> lists = this.listRepository.findByBoardId(id);
                if(lists.isPresent())
                {
                    board.get().setLists(lists.get());
                }
            }catch(Exception e){
                System.out.println("Error loading lists for board "+id);
                e.printStackTrace();
            }            
        }
        return board;
    }    
    
    public Optional<List<BBBoard>> findAll() 
    {
        String sql = SELECT_CLAUSE + ORDER_BY;
        List<BBBoard> clc = jdbcTemplate.query(sql, (rs, rowNum) ->                                
            new BBBoard(
                rs.getLong("id"),                         
                rs.getLong("user_id"),                         
                rs.getString("title"),    
                rs.getString("color"),
                rs.getString("type"), 
                rs.getInt("position"),
                rs.getTimestamp("created").toLocalDateTime(),
                rs.getTimestamp("updated").toLocalDateTime(),
                null
            ));
        return Optional.ofNullable(clc);
    }    
    
    public Optional<List<BBBoard>> findByTitle(String text) 
    {
        //TODO: fix sql
        String sql = SELECT_CLAUSE + " WHERE title LIKE ? " + ORDER_BY;
        List<BBBoard> clc = jdbcTemplate.query(sql, new Object[]{text}, (rs, rowNum) ->                                
            new BBBoard(
                rs.getLong("id"),                         
                rs.getLong("user_id"),                         
                rs.getString("title"),    
                rs.getString("color"),
                rs.getString("type"), 
                rs.getInt("position"),
                rs.getTimestamp("created").toLocalDateTime(),
                rs.getTimestamp("updated").toLocalDateTime(),
                null
            ));
        return Optional.ofNullable(clc);
    }    
    
    public Optional<BBBoard> findNext(long id) 
    {
        System.out.println("Creating sql ");
        String sql = SELECT_CLAUSE + WHERE_ID_GREATER + ORDER_BY + " LIMIT 1";;
        System.out.println("Executing: "+sql);
        
        BBBoard clc = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->                                
            new BBBoard(
                rs.getLong("id"),                         
                rs.getLong("user_id"),                         
                rs.getString("title"),    
                rs.getString("color"),
                rs.getString("type"), 
                rs.getInt("position"),
                rs.getTimestamp("created").toLocalDateTime(),
                rs.getTimestamp("updated").toLocalDateTime(),
                null
            ));
        System.out.println("Returning result");
        return Optional.ofNullable(clc);
        
        /**
         Capcha preadlaze ya empty result set error
         * List<BBBoard> results = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) ->
        new BBBoard(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("title"),
            rs.getString("color"),
            rs.getString("type"),
            rs.getInt("position"),
            rs.getTimestamp("created").toLocalDateTime(),
            rs.getTimestamp("updated").toLocalDateTime(),
            null
        )
    );

    // Return the first result if present, otherwise return an empty Optional
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
         */
        
        
    }    
    
    public Optional<BBBoard> findPrev(long id) 
    {
        String sql = SELECT_CLAUSE +  " WHERE id < ?" + ORDER_BY_INVERSE + " LIMIT 1";;
        BBBoard clc = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->                                
            new BBBoard(
                rs.getLong("id"),                         
                rs.getLong("user_id"),                         
                rs.getString("title"),    
                rs.getString("color"),
                rs.getString("type"), 
                rs.getInt("position"),
                rs.getTimestamp("created").toLocalDateTime(),
                rs.getTimestamp("updated").toLocalDateTime(),
                null
            ));
        return Optional.ofNullable(clc);
    }    
    
    // RowMapper to map result set to BBCard object
    private RowMapper<BBBoard> cboardRowMapper() {
        return (rs, rowNum) -> {
            BBBoard cboard = new BBBoard();
            cboard.setId(rs.getLong("id"));
            cboard.setUserId(rs.getLong("user_id"));
            cboard.setTitle(rs.getString("title"));
            cboard.setColor(rs.getString("color"));
            cboard.setType(rs.getString("type"));
            cboard.setPosition(rs.getInt("position"));
            return cboard;
        };
    }
    
    // Method to update an cboard by ID
    public int update(BBBoard e) {
        String sql = "UPDATE " + TABLE_NAME + " SET "
                + "user_id = ?, "
                + "title = ?, "
                + "color = ?, "
                + "type = ?, "
                + "position = ?, "
                + "updated = CURRENT_TIMESTAMP "
                + WHERE_ID;        
        return jdbcTemplate.update(sql, e.getUserId(), e.getTitle(), e.getColor(), e.getType(), e.getPosition(), e.getId());
    }
    
    //This can be generalized
    public int delete(Long id) 
    {        
        // when deleting board, delete also all child elements (list and card)
        int deletedChildren = this.listRepository.deleteListsForBoard(id);        
        
        String sql = "DELETE FROM " + TABLE_NAME + WHERE_ID;        
         
        return deletedChildren + jdbcTemplate.update(sql, id);
    }
    
}
