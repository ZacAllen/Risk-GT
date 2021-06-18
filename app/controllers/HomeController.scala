package controllers

import javax.inject._
import models._
import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.libs.json.Json._
import play.api.data._
import play.api.data.Forms._
import utils.SQLDriver

import scala.util.Random

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) with play.api.i18n.I18nSupport {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  val gForm = Form(
    mapping(
      "name1" -> nonEmptyText,
      "email1" -> nonEmptyText,
      "name2" -> nonEmptyText,
      "email2" -> nonEmptyText,
      "name3" -> nonEmptyText,
      "email3" -> nonEmptyText,
      "name4" -> text,
      "email4" -> text,
      "name5" -> text,
      "email5" -> text,
      "name6" -> text,
      "email6" -> text
    ) (GameData.apply)(GameData.unapply)
  )

  def victory: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.welcome("Message"))
  }

  def index: Action[AnyContent] = Action { implicit request =>
    Ok(views.html.start_game(gForm))
  }

  /**
    *
    * @return
    */
  def gameInitiate: Action[AnyContent] = Action { implicit request =>
    gForm.bindFromRequest.fold(
      formWithErrors => {
        print("Not Processed")
        Ok(formWithErrors.toString)
      },

      gameData => {
        var list: List[Map[String, Any]] = Nil
        var playerData: Map[String, Any] = Map("id" -> 1, "name" -> gameData.name1, "email" -> gameData.email1)
        list = playerData :: list
        playerData = Map("id" -> 2, "name" -> gameData.name2, "email" -> gameData.email2)
        list = playerData :: list
        playerData = Map("id" -> 3, "name" -> gameData.name3, "email" -> gameData.email3)
        list = playerData :: list
        if (gameData.name4 != "") {
          playerData = Map("id" -> 4, "name" -> gameData.name4, "email" -> gameData.email4)
          list = playerData :: list
          if (gameData.name5 != "") {
            playerData = Map("id" -> 5, "name" -> gameData.name5, "email" -> gameData.email5)
            list = playerData :: list
            if (gameData.name6 != "") {
              playerData = Map("id" -> 6, "name" -> gameData.name6, "email" -> gameData.email6)
              list = playerData :: list
            }
          }
        }

        val playerInitData = gameInitiator(list)
        val sortedPlayers = sortPlayersByTurn(playerInitData)
        val territoryInitData = randomizeTerritories(createTerritories(), sortedPlayers)
        Ok(views.html.game(territoryInitData, sortedPlayers, adjacentMap)).withSession(
          "player_data" -> JsonConverter.toJson(sortedPlayers),
          "territory_data" -> JsonConverter.toJson(territoryInitData)
        )
      }
    )
  }

  def sortPlayersByTurn(players: List[Map[String, Any]]): List[Map[String, Any]] = {
    var sortedPlayers: List[Map[String, Any]] = Nil
    sortedPlayers = players.sortBy(_("turn").toString)
    sortedPlayers
  }

  def addPlayer: Action[JsValue] = Action(parse.json) { implicit request =>
    var newPlayerID = -1
    if ((request.body \ "newGame").as[Boolean]) {
      val gameID = SQLDriver.createGame()
      newPlayerID = SQLDriver.createPlayer(gameID,
        (request.body \ "name").as[String],
        (request.body \ "email").as[String],
        (request.body \ "turn").as[Int])
    } else {
      newPlayerID = SQLDriver.createPlayer((request.body \ "game").as[Int],
        (request.body \ "name").as[String],
        (request.body \ "email").as[String],
        (request.body \ "turn").as[Int])
    }
    Ok(toJson(Map("playerID" -> newPlayerID)))
  }

  def gameInitiator(input: List[Map[String, Any]]): List[Map[String, Any]] = {
    val turnOrder: List[Int] = randomizeTurns(input.length)
    var players: List[Map[String, Any]] = Nil
    var num: Int = 0
    val colors: List[String] = List("Red", "Blue", "Green", "Yellow", "Brown", "Orange")
    val num_armies: Int = initArmiesUnits(input.length)
    for (player_data <- input) {
      val id = turnOrder(num)
      val name = player_data("name").asInstanceOf[String]
      val email = player_data("email").asInstanceOf[String]
      val color = colors(turnOrder(num) - 1)
      val turn = turnOrder(num)
      val territories = List()
      val player = Player(id, name, email, turn, color, num_armies, territories)
      num = num + 1
      val player_map = Map[String, Any]("id" -> player.getId, "name" -> player.getName, "email" -> player.getEmail,
        "color" -> player.getColor, "turn" -> player.getTurn, "numArmies" -> player.getNumArmies)
      players = player_map :: players
    }
    players
  }

  def initArmiesUnits(numPlayers: Int): Int = {
    if (numPlayers == 3) {
      val numArmies = 35
      numArmies
    } else if (numPlayers == 4) {
      val numArmies = 30
      numArmies
    } else if (numPlayers == 5) {
      val numArmies = 25
      numArmies
    } else {
      val numArmies = 20
      numArmies
    }
  }

  def randomizeTurns (playerCount: Int): List[Int] = {
    var turnList: List[Int] = Nil
    var num = 1
    //add 1 - player # to list
    for (_ <- 1 to playerCount) {
      turnList = num :: turnList
      num += 1
    }
    //shuffle
    turnList = util.Random.shuffle(turnList)
    turnList
  }

  /*
  Creates a list of Territory objects, with ID's 1 to 42, and Names "1" to "42"
  We may or may not use the Territory class in the future. We could perhaps modify
  the randomizeTerritories method to map these territories' int ids to each player's
   */

  def createTerritories(): List[Territory] = {
    var territoryList: List[Territory] = Nil
    var name = "1"
    for (inc <- 1 to 42 ) {
      var territory: Territory = new Territory(inc, name, Player(0, "", "", 0, "", 0, Nil),0, 0, District(0, "", Nil), true)
      territoryList = territory :: territoryList
      var nameTemp = name.toInt
      nameTemp = 1 + nameTemp
      name = nameTemp.toString
    }
    territoryList
  }

  //Modified to return a map of id keys mapped to actual Territory objects.
  //Creates a map that contains all the territory ID's (i.e. numbers) as keys, and the Territory objects as values.
  //Each territory object has an owner id that matches to the id of the player that owns it.
  def randomizeTerritories(territoryID: List[Territory], playerID: List[Map[String, Any]]): Map[Int, Territory] = {
    val territories = util.Random.shuffle(territoryID)
    val players = util.Random.shuffle(playerID)

    var map:Map[Int, Territory] = Map()
    var i = 0
    for (a <- 1 to territories.length) {
      map += a -> territories(a - 1)
      val p = players(i % players.length)
      territories(a - 1).setOwnerID(p.get("id"))
      territories(a - 1).setOwnerName(p.get("name"))
      territories(a - 1).addUnits(1)
      i += 1
    }
    map
  }

  //Returns the # of armies the player is allowed to place at the start of their turn
  def newArmies(playerID: Int, territories: Map[Int, Territory]): Int = {
    //Based on the Rules on the Google doc, the # of new armies is the # of territories / 3. If < 3 territories they
    //Get 1 army
    var numTerritories = 0
    for ((_, value) <- territories) {
      if (value.getOwnerID == playerID) numTerritories += 1
    }
    if (numTerritories < 3) {
      1
    } else {
      numTerritories / 3
    }
  }

  def placeSingleArmy(territory: Territory): Unit = {
    territory.addUnits(1)
  }

  //Randomizes a player's territories based on the # of armies to add
  def randomizeArmies(territories: List[Territory], numArmies: Int): Unit = {
    val random = new Random
    for (_ <- 1 to numArmies) {
      territories(random.nextInt(territories.length)).addUnits(1)
    }
  }

  //Set the turns for players using the random list generated and the player list
  def setPlayerTurn(players: List[Player], turnList: List[Int]) {
    if (players != Nil && turnList != Nil) {
      players.head.setTurn(turnList.head)
      setPlayerTurn(players.tail, turnList.tail)
    }
  }



  //Bonus for occupying the whole district
  val REDBONUS = 5
  val BROWNBONUS = 5
  val BLUEBONUS = 5
  val GREENBONUS = 5
  val ORANGEBONUS = 5
  val YELLOWBONUS = 5

//  def addArmies(player: Player) {
//    if (player.getTerritories.length <= 2) {
//      player.addArmyUnits(1)
//    }  else {
//      player.addArmyUnits(player.getTerritories.length/3)
//      if (player.getTerritories.count(_.getId <= 7) == 7) {
//        player.addArmyUnits(REDBONUS)
//      } else if (player.getTerritories.count(x => x.getId >= 8 && x.getId <= 13) == 6) {
//        player.addArmyUnits(BROWNBONUS)
//      } else if (player.getTerritories.count(x => x.getId >= 14 && x.getId <= 21) == 8) {
//        player.addArmyUnits(BLUEBONUS)
//      } else if (player.getTerritories.count(x => x.getId >= 22 && x.getId <= 29) == 8) {
//        player.addArmyUnits(GREENBONUS)
//      } else if (player.getTerritories.count(x => x.getId >= 30 && x.getId <= 36) == 7) {
//        player.addArmyUnits(ORANGEBONUS)
//      } else if (player.getTerritories.count(x => x.getId >= 37 && x.getId <= 42) == 6) {
//        player.addArmyUnits(YELLOWBONUS)
//      }
//    }
//  }

  def addArimies: Action[JsValue] = Action(parse.json) { implicit request =>
    val playerID: Int = (request.body \ "playerID").as[Int]
    val gameID: Int = (request.body \ "gameID").as[Int]
    var numArmies: Int = 0
    var playerTerritoryList = SQLDriver.getPlayerTerritories(gameID, playerID)
    if (playerTerritoryList.length <= 2) {
      numArmies = 1
    } else {
      var playerDetails = SQLDriver.getPlayerDetails(playerID)
      numArmies = playerDetails(playerID)._4 / 3
    }
    if (playerTerritoryList.count(_ <= 7) == 7) {
      numArmies += REDBONUS
    } else if (playerTerritoryList.count(x => x >= 8 && x <= 13) == 6) {
      numArmies += BROWNBONUS
    } else if (playerTerritoryList.count(x => x >= 14 && x <= 21) == 8) {
      numArmies += BLUEBONUS
    } else if (playerTerritoryList.count(x => x >= 22 && x <= 29) == 8) {
      numArmies += GREENBONUS
    } else if (playerTerritoryList.count(x => x >= 30 && x <= 36) == 7) {
      numArmies += ORANGEBONUS
    } else if (playerTerritoryList.count(x => x >= 37 && x <= 42) == 6) {
      numArmies += YELLOWBONUS
    }
    SQLDriver.updatePlayerArmies(gameID, playerID, numArmies)
    Ok(toJson(Map("armies_added" -> numArmies)))
  }

//Add a new Soldier to a territory.
//  def placeNewArmies(player: Player, soldier: Soldier, territory: Territory, numArmies: Int): Unit = {
//      for (i <- 1 to numArmies) {
//        territory.addSoldier(soldier)
//        territory.setSoldiers(territory.getSoldiers.sorted)
//        soldier.setTerritory(territory)
//        player.addArmyUnits(-soldier.getPrice)
//      }
//  }

  def placeNewArmies: Action[JsValue] = Action(parse.json) { implicit request =>
    val playerID: Int = (request.body \ "playerID").as[Int]
    val gameID: Int = (request.body \ "gameID").as[Int]
    val territoryID: Int = (request.body \ "territoryID").as[Int]
    val numArmies: Int = (request.body \ "numArmies").as[Int]
    SQLDriver.updateTerritory(territoryID, playerID, numArmies)
    SQLDriver.updatePlayerArmies(gameID, playerID, -numArmies)
    Ok(toJson(Map("armies placed" -> numArmies)))
  }

  //roll a dice and return the number
  def rollDice(): Int = {
    val random = new Random()
    1 + random.nextInt(6)
  }

  //allow one Territory to attack another and return the attacker and the defender territories as a List
//  def attack(attacker: Territory, defender: Territory): List[Territory] = {
//    var attackerDices = List[Int]()
//    var defenderDices = List[Int]()
//
//    for (i <- 1 until attacker.getUnits) {
//      rollDice() :: attackerDices
//    }
//
//    for (j <- 1 to defender.getUnits) {
//      rollDice() :: defenderDices
//    }
//
//    attackerDices = attackerDices.sorted.reverse
//    defenderDices = defenderDices.sorted.reverse
//
//    for (k <- 1 to math.min(attackerDices.length, defenderDices.length)) {
//      if (attackerDices.head > defenderDices.head) {
//        defender.eliminateSoldier
//        defender.addUnits(-1)
//      } else if (attackerDices.head < defenderDices.head) {
//        attacker.eliminateSoldier
//        attacker.addUnits(-1)
//      }
//      attackerDices = attackerDices.tail
//      defenderDices = defenderDices.tail
//    }
//
//    attacker.setCanAttack(false)
//
//    List(attacker, defender)
//  }


  //The adjacent Map of the game
  val adjacentMap = Map((1,List(2,3,5,6)),(2,List(1,3,4,8)),(3,List(1,2,4,5,7)),(4,List(2,3,5,7)),(5,List(1,3,4,6,7)),
    (6,List(1,5,7,22)),(7,List(3,4,5,6,22,30)),(8,List(2,9)),(9,List(8,10,11,14)),(10,List(9,11)),(11,List(9,10,12,13)),
    (12,List(11,13,16)),(13,List(17,16,12,11,33,35)),(14,List(9,15,16)),(15,List(14,16,18)),(16,List(14,15,18,19,17,35,20,12,13)),
    (17,List(13,16,33,35)),(18,List(15,16,19,20,21)),(19,List(16,18,20,21)),(20,List(19,21,35,38)),(21,List(18,19,20,40)),
    (22,List(6,7,23)),(23,List(22,24)),(24,List(23,25)),(25,List(24,26)),(26,List(25,28)),(27,List(28)),(28,List(26,27,32,29)),
    (29,List(34,39,28)),(30,List(7,31,33)),(31,List(30,33,32)),(32,List(31,33,34,28)),(33,List(30,31,32,34,35)),(34,List(32,33,36,37,39,29)),
    (35,List(37,33,36,20,19,16,17)),(36,List(34,35)),(37,List(35,34,38,39)),(38,List(37,40,39,41)),(39,List(37,38,40,41,42,34)),
    (40,List(21,20,38,39,41)),(41,List(39,38,40)),(42,List(39)))

  //Check if the two territories are adjacent
  def isAdjacent(attacker: Territory, defender:Territory): Boolean = {
    adjacentMap(attacker.getId).contains(defender.getId)
  }
}
