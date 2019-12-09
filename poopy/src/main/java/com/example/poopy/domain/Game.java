package com.example.poopy.domain;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Game {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long gameid;

	@Column(name="questions", columnDefinition="CLOB")
	private String[] questions=new String[6];
	private String code;
	private String question;
	private Long judgeindex;
	private int round = 1;
	private boolean started=false;

	@JsonIgnore
	@Column(name="cards", columnDefinition="CLOB")
	private String[] cards;
	
	@JsonIgnore
	private boolean[] cardsDist;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
	private List<Player> players = new ArrayList<Player>();
	
	public Game() {
		code=genCode();
	}

	public void addPlayer(Player player) {
		this.players.add(player);
	}
	
	//the judge sends a post to choose the question being played
	public void chooseQuestion(String question, PlayerRepository repo) {
		if(!started)
			return;

		this.question=question;
		for (int i = 0; i < players.size(); i++) {
			players.get(i).setAnswer(null);
			repo.save(players.get(i));
		}
	}
	
	//when player send a POST to give his answer
	public void Answer(String answer) {
		for (int j = 0; j < players.size(); j++) {
			if(players.get(j).getPlayerid()==Long.parseLong(answer.substring(0, answer.indexOf(':')))) {
				if (players.get(j).getAnswer()==null) {
					players.get(j).setAnswer(answer);
					return;
				}
			}
		}
	}
	
	public void chooseWinner(PlayerRepository prepo, String[] questions, Long winnerid) {
		//get who the winner is
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			p.setLastWinner(false);
			if(players.get(i).getPlayerid()==winnerid) {
				p.point();
				p.setLastWinner(true);
				prepo.save(p);
			}
		}
		
		
		//cards played are available again
		for (int i = 0; i < cards.length; i++) {
			for (int j = 0; j < players.size(); j++) {
				if(players.get(j).getAnswer()!=null && players.get(j).getAnswer().equals(cards[i]))
					cardsDist[i]=false;
			}
		}
		
		//distribute one card per player
		for (int i = 0; i < players.size(); i++) {
			int rand = (int)(Math.random()*this.cards.length);
			if(this.cardsDist[rand]==false) {
				Player p = players.get(i);
				if(p.getPlayerid()!=judgeindex) {
					p.addCard(this.cards[rand]);
					prepo.save(p);
					this.cardsDist[rand]=true;
				}
			}
			else
				i--;
		}

		
		//next judge
		for (int i = 0; i < players.size()-1; i++) {
			if (players.get(i).getPlayerid()==judgeindex) {
				judgeindex=players.get(i+1).getPlayerid();
				break;
			}
			judgeindex=players.get(0).getPlayerid();
		}
		
		//change round
		round++;
		question=null;
		this.questions=questions;
	}

	public void StartGame(PlayerRepository prepo, String[] questions, String[] cards) {
		this.questions=questions;
		int judge=(int)((Math.random())*this.players.size());
		this.judgeindex=players.get(judge).getPlayerid();
		distributeCards(prepo, cards);
		this.started=true;
	}
	
	private String genCode() {
	    byte[] array = new byte[7]; // length is bounded by 7
	    new Random().nextBytes(array);
	    String generatedString = new String(array, Charset.forName("ASCII"));
	    return generatedString;
	}
	
	//used to distribute cards in the beginning
	private void distributeCards(PlayerRepository prepo, String[] cards) {
		this.cards=cards;
		this.cardsDist=new boolean[cards.length];
		for (boolean b : cardsDist) {
			b=false;
		}
		
		for (int i = 0; i < this.players.size(); i++) {
			for (int j = 0; j < 6; j++) {
				int rand = (int)(Math.random()*this.cards.length);
				if(this.cardsDist[rand]==false) {
					this.players.get(i).addCard(this.cards[rand]);
					this.cardsDist[rand]=true;
				}
				else
					j--;
			}
			prepo.save(players.get(i));
		}
	}

	public Long getGameid() {
		return gameid;
	}

	public void setGameid(Long gameid) {
		this.gameid = gameid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Long getJudgeindex() {
		return judgeindex;
	}

	public void setJudgeindex(Long judgeindex) {
		this.judgeindex = judgeindex;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public String[] getQuestions() {
		return questions;
	}

	public void setQuestions(String[] questions) {
		this.questions = questions;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public String[] getCards() {
		return cards;
	}

	public void setCards(String[] cards) {
		this.cards = cards;
	}

	public boolean[] getCardsDist() {
		return cardsDist;
	}

	public void setCardsDist(boolean[] cardsDist) {
		this.cardsDist = cardsDist;
	}
	
	
}


