package com.imaginationoverboard.bouncergbleaderboard.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response format for Unity to be able to deserialize
 *
 * @author Alvin Tan (AT)
 */
@Getter
@Setter
@AllArgsConstructor
public class HighscoreList {

    private List<LeaderboardEntry> highscoreList;
}
