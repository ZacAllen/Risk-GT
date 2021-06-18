package utils

import java.sql._
import java.text.SimpleDateFormat
import java.util.Calendar

object SQLDriver {

  // Get connection to database
  val myConn: Connection = DriverManager.getConnection("jdbc:mysql://127.0.0.2:3306/world", "root", "root")

  // Simple DB statement
  val myStatement: Statement = myConn.createStatement()

  var statement: String = ""

  def createGame(): Int = {
    val currDateTime: String = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").
      format(Calendar.getInstance().getTime)
    statement = "INSERT INTO game " + " (timestamp) " +
      String.format(" VALUES ('%s')", currDateTime)
    myStatement.executeUpdate(statement)
    val checkerStatement = "SELECT MAX(id) FROM game"
    val checkerResult = myStatement.executeQuery(checkerStatement)
    checkerResult.getInt(0)
  }

  def createPlayer(gameID: Int, playerName: String, playerEmail: String, turnNum: Int): Int = {
    val currDateTime: String = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").
      format(Calendar.getInstance().getTime)
    statement = "INSERT INTO player " + " (name, email, turn, num_armies, game_id, timestamp) " +
      f" VALUES ('$playerName', '$playerEmail', $turnNum, 0, $gameID, '$currDateTime')"
    myStatement.executeUpdate(statement)
    val checkerStatement = "SELECT MAX(id) FROM player"
    val checkerResult = myStatement.executeQuery(checkerStatement)
    checkerResult.getInt(0)
  }

  def updatePlayerArmies(gameID: Int, playerID: Int, numArmies: Int): Unit = {
    statement = "UPDATE player " +
      f" SET num_armies = $numArmies " +
      f" WHERE id = $playerID AND game_id = $gameID"
    myStatement.executeUpdate(statement)
  }

  def createTerritory(gameID: Int, playerID: Int, territoryActID: Int): Int = {
    statement = "INSERT INTO territory " + " (game_id, player_id, territory_act_id, army) " +
      f" VALUES ($gameID, $playerID, $territoryActID, 1)"
    myStatement.executeUpdate(statement)
    val checkerStatement = "SELECT MAX(id) FROM territory"
    val checkerResult = myStatement.executeQuery(checkerStatement)
    checkerResult.getInt(0)
  }

  def updateTerritory(territoryID: Int, playerID: Int, numArmy: Int): Unit = {
    statement = "UPDATE territory " +
      f" SET player_id = $playerID, army = $numArmy " +
      f" WHERE id = $territoryID"
    myStatement.executeUpdate(statement)
  }

  def getTerritories(gameID: Int): Map[Int, (Int, Int)] = {
    statement = "SELECT * FROM territory " +
      f" WHERE game_id = $gameID"
    val checkerResult = myStatement.executeQuery(statement)
    var territoryMap: Map[Int, (Int, Int)] = Map.empty[Int, (Int, Int)]
    while (checkerResult.next()) {
      territoryMap += (checkerResult.getInt("territory_act_id") -> (checkerResult.getInt("player_id"), checkerResult.getInt("army")))
    }
    checkerResult.close()
    territoryMap
  }

  def getPlayers(gameID: Int): Map[Int, (String, String, Int, Int)] = {
    statement = "SELECT * FROM player " +
      f" WHERE game_id = $gameID"
    val checkerResult = myStatement.executeQuery(statement)
    var playerMap: Map[Int, (String, String, Int, Int)] = Map.empty[Int, (String, String, Int, Int)]
    while (checkerResult.next()) {
      playerMap += (checkerResult.getInt("turn") -> Tuple4(checkerResult.getString("name"),
        checkerResult.getString("email"),
        checkerResult.getInt("id"),
        checkerResult.getInt("num_armies")))
    }
    checkerResult.close()
    playerMap
  }

  def getPlayerDetails(id: Int): Map[Int, (String, String, Int, Int)] = {
    statement = "SELECT * FROM player " +
      f" WHERE id = $id"
    val checkerResult = myStatement.executeQuery(statement)
    var playerMap: Map[Int, (String, String, Int, Int)] = Map.empty[Int, (String, String, Int, Int)]
    while (checkerResult.next()) {
      playerMap += (checkerResult.getInt("id") -> Tuple4(checkerResult.getString("name"),
        checkerResult.getString("email"),
        checkerResult.getInt("turn"),
        checkerResult.getInt("num_armies")))
    }
    checkerResult.close()
    playerMap
  }

  def getTerritoryDetails(gameID: Int, territoryActID: Int): Map[Int, (Int, Int)] = {
    statement = "SELECT * FROM territory " +
      f" WHERE game_id = $gameID AND territory_act_id = $territoryActID"
    val checkerResult = myStatement.executeQuery(statement)
    var territoryMap: Map[Int, (Int, Int)] = Map.empty[Int, (Int, Int)]
    while (checkerResult.next()) {
      territoryMap += (checkerResult.getInt("territory_act_id") -> (checkerResult.getInt("player_id"), checkerResult.getInt("army")))
    }
    checkerResult.close()
    territoryMap
  }

  def increaseTurns(gameID: Int): Unit = {
    statement = "SELECT num_turns FROM game " + f" WHERE id = $gameID"
    val checkerResult = myStatement.executeQuery(statement)
    val numTurns = checkerResult.getInt("num_turns") + 1
    statement = "UPDATE game " +
      f" SET num_turns = $numTurns " +
      f" WHERE id = $gameID"
    myStatement.executeUpdate(statement)
    checkerResult.close()
  }

  def getPlayerTerritories(gameID: Int, playerID: Int): List[Int] = {
    statement = "SELECT * FROM territory " +
      f" WHERE game_id = $gameID AND player_id = $playerID"
    val checkerResult = myStatement.executeQuery(statement)
    var territoryList: List[Int] = Nil
    while (checkerResult.next()) {
      territoryList = checkerResult.getInt("territory_act_id") :: territoryList
    }
    checkerResult.close()
    territoryList
  }
}
