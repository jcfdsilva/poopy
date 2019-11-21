package com.example.poopy.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {
	List<Player> findByPlayerid(String playerid);
}