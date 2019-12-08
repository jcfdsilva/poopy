package com.example.poopy.domain.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.poopy.domain.Game;
import com.example.poopy.domain.GameRepository;
import com.example.poopy.domain.Player;
import com.example.poopy.domain.PlayerRepository;

@Controller
public class PoopyController {

	@Autowired
	private GameRepository grepository; 
	@Autowired
	private PlayerRepository prepository; 
	
	List<String> cards = new ArrayList<String>();
	List<String> questions = new ArrayList<String>();
	
	PoopyController(){
		cards.add("card1");
		cards.add("card2");
		cards.add("card3");
		cards.add("card4");
		cards.add("card5");
		cards.add("card6");
		cards.add("card7");
		cards.add("card8");
		cards.add("card9");
		cards.add("card10");
		cards.add("card11");
		cards.add("card12");
		cards.add("card13");
		cards.add("card14");
		cards.add("card15");
		cards.add("card16");
		cards.add("card17");
		cards.add("card18");
		cards.add("card19");
		cards.add("card20");
		cards.add("card21");
		cards.add("card22");
		cards.add("card23");
		cards.add("card24");
		//poih
		questions.add("quest1");
		questions.add("quest2");
		questions.add("quest3");
		questions.add("quest4");
		questions.add("quest5");
		questions.add("quest6");
	}
	
    @RequestMapping(value={"/"})
	public String index(Model model) {
    	model.addAttribute("questions", questions);
    	model.addAttribute("answers", cards);
		return "index";
	}
    
    @RequestMapping(value={"/addAns"})
	public String addAns(Model model) {
    	model.addAttribute("answer", new String());
		return "addAns";
	}
    
    @RequestMapping(value = "/saveAns", method = RequestMethod.POST)
    public String saveAns(String answer){
    	System.out.println(answer);
        cards.add(answer);
        return "redirect:/";
    }
    
    @RequestMapping(value = "/saveQuest", method = RequestMethod.POST)
    public String save(String question){

    	System.out.println(question);
        questions.add(question);
        return "redirect:/";
    }
    
    @RequestMapping(value={"/addQuest"})
	public String addQuest(Model model) {
    	String n = "";
    	model.addAttribute("question", n);
		return "addQuest";
	}
    
    //how to login in postman?
    @RequestMapping(value={"/login"})
   	public String login() {
   		return "login";
   	}
    
    
    @RequestMapping(value={"/newGame"}, method = RequestMethod.POST)
	public @ResponseBody Long newGame(@RequestBody String name) {
    	Game g = new Game();
    	grepository.save(g);
    	Player p = new Player(name,true);
    	p.setGame(g);
    	prepository.save(p);
		return g.getGameid();
	}
    
    @RequestMapping(value={"/chooseQuest/{id}"}, method = RequestMethod.POST)
	public @ResponseBody boolean chooseQuestion(@PathVariable("id") Long id, @RequestBody String quest) {
    	Optional<Game> game = grepository.findById(id);
    	if(!game.isPresent())
    		return false;
    	game.ifPresent(g -> {
    		g.chooseQuestion(quest,prepository);
    		grepository.save(g);
    	});
    	return true;
	}
    
    @RequestMapping(value={"/answer/{id}"}, method = RequestMethod.POST)
	public @ResponseBody boolean answer(@PathVariable("id") Long id, @RequestBody String answer) {
    	Optional<Game> game = grepository.findById(id);
    	Long playerid = Long.parseLong(answer.substring(0,answer.indexOf(':')));
    	Optional<Player> player = prepository.findById(playerid);
    	if(!game.isPresent())
    		return false; 
    	if(!player.isPresent())
    		return false;
    	
    	game.ifPresent(g -> {
    		player.ifPresent(p->{
    			boolean x = p.erase(answer.substring(answer.indexOf(':')+1),g.getGameid());
    			
    			if(x) {
    				p.setAnswer(answer.substring(answer.indexOf(':')+1));
    				prepository.save(p);
    			}
    		});
    	});
    	return true;
	}
    
 // RESTful service to get game by id
    @RequestMapping(value="/game/{id}", method = RequestMethod.GET)
    public @ResponseBody Optional<Game> findGameRest(@PathVariable("id") Long id) {	
    	return grepository.findById(id);
    }
    
    @RequestMapping(value="/deleteQuest/{quest}", method = RequestMethod.GET)
    public String deleteQuest(@PathVariable("quest") String quest) {	
    	for (int i = 0; i < questions.size(); i++) {
			if(questions.get(i).equals(quest)){
				questions.remove(i);
		    	return "redirect:/";
			}
		}
    	return "redirect:/";
    }
    
 // RESTful service to get all games
    @RequestMapping(value="/games", method = RequestMethod.GET)
    public @ResponseBody List<Game> gameListRest() {
        return (List<Game>) grepository.findAll();
    }
    
    // RESTful service to ask to join a game
    @RequestMapping(value="/join/{id}", method = RequestMethod.POST)
    public @ResponseBody Long joinGameRest(@PathVariable("id") Long id, @RequestBody String name) {
    	Optional<Game> game = grepository.findById(id);
    	if(!game.isPresent())
    		return null;

		Player player = new Player(name,false);
    	game.ifPresent(g -> {
    		if(!g.isStarted()) {
	        	player.setGame(g);
	        	prepository.save(player);
    		}
    	});

    	return player.getPlayerid();
    }


    @RequestMapping(value="/chooseWinner/{id}", method = RequestMethod.POST)
    public @ResponseBody boolean chooseWinner(@PathVariable("id") Long id, @RequestBody String winnerid) {
    	Optional<Game> game = grepository.findById(id);
    	if(!game.isPresent())
    		return false;
    	
    	game.ifPresent(g -> {
    		if(g.isStarted()) {
        		String[]questions = pickSixQuest();
    			g.chooseWinner(prepository, questions, Long.parseLong(winnerid));
    			grepository.save(g);
    		}
    	});
    	return true;
    }
    
 // RESTful service to ask to start a game
    @RequestMapping(value="/startGame/{id}", method = RequestMethod.GET)
    public @ResponseBody boolean startGameRest(@PathVariable("id") Long id) {
    	Optional<Game> game = grepository.findById(id);
    	game.ifPresent(g -> {
    		String[]questions = pickSixQuest();
    		String[] cards= new String[this.cards.size()];
    		for (int i = 0; i < cards.length; i++) {
				cards[i]=this.cards.get(i);
			}
    	    g.StartGame(prepository, questions, cards);
    	    grepository.save(g);
    	});
    	if(game.isPresent())
    		return true;
    	else
    		return false;
    }
    
    private String[] pickSixQuest() {
    	String[] questions = new String[6];
    	boolean[] questionsUsed = new boolean[this.questions.size()];
    	
    	for (int i = 0; i < questions.length; i++) {
        	int x = (int)(Math.random()*this.questions.size());
        	System.out.println(x);
        	if(!questionsUsed[x]) {
        		questions[i]=this.questions.get(x);
        		questionsUsed[x]=true;
        	}
        	else i--;
		}
    	return questions;
    }
    
    class Answer{

		public String answer;
		public Long id;
    	
    }
}