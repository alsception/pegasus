package org.alsception.pegasus.features.bootboard.services;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.alsception.pegasus.features.bootboard.entities.BBBoard;
import org.alsception.pegasus.features.bootboard.entities.BBList;
import org.alsception.pegasus.features.bootboard.repositories.BBBoardRepository;
import org.alsception.pegasus.features.bootboard.repositories.BBCardRepository;
import org.alsception.pegasus.features.bootboard.repositories.BBListRepository;
import org.alsception.pegasus.features.bootboard.utils.UniqueIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateEngine 
{
    
    @Autowired
    private BBCardRepository cardRepository;
    
    @Autowired
    private BBListRepository listRepository;
    
    @Autowired
    private BBBoardRepository boardRepository;
    
    public BBBoard createTemplate_DailyBoard(BBBoard board) throws Exception
    {
        //1) create board
        //2. create list name TODO
        //3. create list name DONE
        //4. create list name in progress
        //5. create list name other
        //6. create list name backlog
    
        if(board == null) board = new BBBoard();
        
        board.setTitle(board.getTitle() + " - DAILY BOARD");
        board.setId(UniqueIdGenerator.generateNanoId());
        
        this.boardRepository.save(board);
        
        String listNames[] = {"🛠️ TODO", "DONE 😎", "IN PROGRESS ⚙️", "BACKLOG", "OTHER", "SKIP/Problem"};
        String colors[] = {"white", "green", "blue", "black", "orange", "red"};
        
        ArrayList<BBList> lists = new ArrayList<>();
        
        for (int i = 0; i < listNames.length; i++) {
            BBList l;
            try {
                l = this.createList(board.getId(), listNames[i], colors[i]);
                lists.add( l );
            } catch (Exception ex) {
                Logger.getLogger(TemplateEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
           
        }
        
        board.setLists(lists);
        System.out.println("Template board created: "+board.getTitle() + " | " + board.getId());
        return board;
    }
    
    public BBList createList(long boardId, String name, String color) throws Exception
    {
        BBList list = new BBList(name);
        
        list.setUserId(1l); //TODO: manage users ASAP
        list.setBoardId(boardId);
        list.setColor(color);
        
        return this.listRepository.create(list);
    }
    
}
