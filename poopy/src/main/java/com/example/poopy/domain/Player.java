package com.example.poopy.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Player {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long playerid;
	private String[] cards = new String[6];
	private int score;
	private String name;
	private String answer;
	private boolean lastWinner =false;
	private boolean isCreator = false;
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "gameid")
	private Game game;
	
	public Player() {
		this.score=0;
	}

	public Player(String name, boolean creator) {
		this.score=0;
		this.name=name;
		isCreator=creator;
	}
	
	//adds a card in the first null slot availble
	public void addCard(String card) {
		for (int i = 0; i < cards.length; i++) {
			if(cards[i]==null) {
				cards[i]=card;
				return;
			}
		}
	}

	//erases one card with the String requested but only if player is in the game
	public boolean erase(String card, Long gameId) {
		if(gameId!=game.getGameid())
			return false;
		
		for (int i = 0; i < cards.length; i++) {
			if(cards[i].equals(card)) {
				cards[i]=null;
				return true;
			}
		}
		return false;
	}
	
	public void point() {
		score++;
	}

	public Long getPlayerid() {
		return playerid;
	}

	public void setPlayerid(Long playerid) {
		this.playerid = playerid;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public String[] getCards() {
		return cards;
	}

	public void setCards(String[] cards) {
		this.cards = cards;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isLastWinner() {
		return lastWinner;
	}

	public void setLastWinner(boolean lastWinner) {
		this.lastWinner = lastWinner;
	}

	public boolean isCreator() {
		return isCreator;
	}

	public void setCreator(boolean isCreator) {
		this.isCreator = isCreator;
	}
	
}
