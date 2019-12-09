package com.example.poopy;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.poopy.domain.GameRepository;
import com.example.poopy.domain.Player;
import com.example.poopy.domain.PlayerRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PlayerRepositoryTest {

	@Autowired
    private GameRepository grepository;
	@Autowired
	private PlayerRepository prepository;
	
	@Test
    public void createNewGame() {
    	Player player = new Player("test",true);
    	prepository.save(player);
    	assertThat(player.getPlayerid()).isNotNull();
    }
	
	@Test
    public void addCard() {
    	Player player = new Player("test",true);
    	player.addCard("test");
    	prepository.save(player);
    	assertThat(player.getCards()[0]).isNotNull();
    }
}
