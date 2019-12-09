package com.example.poopy;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.poopy.domain.Game;
import com.example.poopy.domain.GameRepository;
import com.example.poopy.domain.Player;
import com.example.poopy.domain.PlayerRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class GameRepositoryTest {
	@Autowired
    private GameRepository grepository;
	@Autowired
	private PlayerRepository prepository;

	private String[] questions= {"test1","test2","test3","test4","test5","test6"};
	private String[] answers= {"test1","test2","test3","test4","test5","test6","test1","test2","test3","test4","test5","test6","test1","test2","test3","test4","test5","test6"};
	@Test
    public void createNewGame() {
    	Game game = new Game();
    	grepository.save(game);
    	assertThat(game.getGameid()).isNotNull();
    }
	
	@Test
    public void addNewPlayer() {
    	Game game = new Game();
    	assertThat(game.getPlayers().size()==0);
    	game.addPlayer(new Player("test",true));
    	grepository.save(game);
    	assertThat(game.getPlayers().size()>0);
    }
	
	@Test
    public void chooseQuestion() {
    	Game game = new Game();
    	grepository.save(game);    	
    	assertThat(game.getQuestion()).isNull();
    	
    	game.addPlayer(new Player("test",true));
    	game.chooseQuestion("question", prepository);
    	grepository.save(game);
    	assertThat(game.getQuestion()).isNull();
    	
    	game.StartGame(prepository, questions, answers);
    	game.chooseQuestion("question", prepository);
    	grepository.save(game);
    	assertThat(game.getQuestion()).isNotNull();
    }
	
	@Test
    public void Answer() {
    	Game game = new Game();
    	Player p = new Player("test",true);
    	game.addPlayer(p);
    	game.addPlayer(new Player("test",true));
    	game.addPlayer(new Player("test",true));
    	game.StartGame(prepository, questions, answers);
    	game.chooseQuestion("question", prepository);
    	game.Answer(p.getPlayerid()+":"+p.getCards()[0]);
    	prepository.save(p);
    	grepository.save(game);
    	assertThat(p.getAnswer()).isNotNull();
    }
	
	@Test
    public void chooseWinner() {
    	Game game = new Game();
    	Player p = new Player("test",true);
    	game.addPlayer(p);
    	game.StartGame(prepository, questions, answers);
    	game.chooseQuestion("question", prepository);
    	game.Answer(p.getPlayerid()+":"+p.getCards()[0]);
    	game.chooseWinner(prepository, questions, p.getPlayerid());
    	prepository.save(p);
    	grepository.save(game);
    	assertThat(p.isLastWinner()==true);
    	assertThat(p.getScore()==1);
    }
}
