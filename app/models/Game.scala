package models

class Game(players: List[Player], territories: List[Territory]) {

  def getPlayerList(): List[Player] = {
    val player_list: List[Player] = Nil
    players.foreach {_.getName :: player_list}
    player_list
  }

  def getTerritoryList(): List[Territory] = {
    val territory_list: List[Territory] = Nil
    territories.foreach {_.getId :: territory_list}
    territory_list
  }
}

object Game {
  def apply(players: List[Player], territories: List[Territory]) : Any = new Game(players, territories)
}
