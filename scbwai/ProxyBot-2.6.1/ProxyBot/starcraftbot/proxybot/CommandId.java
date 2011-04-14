package starcraftbot.proxybot;


public class CommandId {

  /**
	 * Race 
	 */
	public enum Race {
		Zerg(0), 
    Terran(1),
    Protoss(2),
    Random(3),
    Other(4),
    None(5),
    Unknown(6);

    int raceId;

    Race(int num){
      raceId = num;
    }

    public int getNumValue(){
      return raceId;
    }
	}

	/**
	 * Orders (commands)
	 */
	public enum Order {
		Die(0),
    Stop(1),
    Guard(2),
    PlayerGuard(3),
    TurretGuard(4),
    BunkerGuard(5),
    Move(6),
    ReaverStop(7),
    Attack1(8),
    Attack2(9),
    AttackUnit(10), 
    AttackFixedRange(11), 
    AttackTile(12), 
    Hover(13), 
    AttackMove(14), 
    InfestMine1(15), 
    Nothing1(16), 
    Powerup1(17), 
    TowerGuard(18), 
    TowerAttack(19), 
    VultureMine(20), 
    StayinRange(21), 
    TurretAttack(22), 
    Nothing2(23), 
    Nothing3(24), 
    DroneStartBuild(25), 
    DroneBuild(26), 
    InfestMine2(27), 
    InfestMine3(28), 
    InfestMine4(29), 
    BuildTerran(30), 
    BuildProtoss1(31), 
    BuildProtoss2(32), 
    ConstructingBuilding(33), 
    Repair1(34), 
    Repair2(35), 
    PlaceAddon(36), 
    BuildAddon(37), 
    Train(38), 
    RallyPoint1(39), 
    RallyPoint2(40), 
    ZergBirth(41), 
    Morph1(42), 
    Morph2(43), 
    BuildSelf1(44), 
    ZergBuildSelf(45), 
    Build5(46), 
    Enternyduscanal(47), 
    BuildSelf2(48), 
    Follow(49), 
    Carrier(50), 
    CarrierIgnore1(51), 
    CarrierStop(52), 
    CarrierAttack1(53), 
    CarrierAttack2(54), 
    CarrierIgnore2(55), 
    CarrierFight(56), 
    HoldPosition1(57), 
    Reaver(58), 
    ReaverAttack1(59), 
    ReaverAttack2(60), 
    ReaverFight(61), 
    ReaverHold(62), 
    TrainFighter(63), 
    StrafeUnit1(64), 
    StrafeUnit2(65), 
    RechargeShields1(66), 
    Rechargeshields2(67), 
    ShieldBattery(68), 
    Return(69), 
    DroneLand(70), 
    BuildingLand(71), 
    BuildingLiftoff(72), 
    DroneLiftoff(73), 
    Liftoff(74), 
    ResearchTech(75), 
    Upgrade(76), 
    Larva(77), 
    SpawningLarva(78), 
    Harvest1(79), 
    Harvest2(80), 
    MoveToGas(81), // Unit is moving to refinery
    WaitForGas(82), // Unit is waiting to enter the refinery (another unit is // currently in it)
		HarvestGas(83), // Unit is in refinery
		ReturnGas(84), // Unit is returning gas to center
		MoveToMinerals(85), // Unit is moving to mineral patch
		WaitForMinerals(86), // Unit is waiting to use the mineral patch (another // unit is currently mining from it)
		MiningMinerals(87), // Unit is mining minerals from mineral patch
		Harvest3(88), 
    Harvest4(89), 
    ReturnMinerals(90), // Unit is returning minerals to  center
		Harvest5(91), 
    EnterTransport(92), 
    Pickup1(93), 
    Pickup2(94), 
    Pickup3(95), 
    Pickup4(96), 
    Powerup2(97), 
    SiegeMode(98), 
    TankMode(99), 
    WatchTarget(100), 
    InitCreepGrowth(101), 
    SpreadCreep(102), 
    StoppingCreepGrowth(103), 
    GuardianAspect(104), 
    WarpingArchon(105), 
    CompletingArchonsummon(106), 
    HoldPosition2(107), 
    HoldPosition3(108), 
    Cloak(109), 
    Decloak(110), 
    Unload(111), 
    MoveUnload(112), 
    FireYamatoGun1(113), 
    FireYamatoGun2(114), 
    MagnaPulse(115), 
    Burrow(116), 
    Burrowed(117), 
    Unburrow(118), 
    DarkSwarm(119), 
    CastParasite(120), 
    SummonBroodlings(121), 
    EmpShockwave(122), 
    NukeWait(123), 
    NukeTrain(124), 
    NukeLaunch(125), 
    NukePaint(126), 
    NukeUnit(127), 
    NukeGround(128), 
    NukeTrack(129), 
    InitArbiter(130), 
    CloakNearbyUnits(131), 
    PlaceMine(132), 
    Rightclickaction(133), 
    SapUnit(134), 
    SapLocation(135), 
    HoldPosition4(136), 
    Teleport(137), 
    TeleporttoLocation(138), 
    PlaceScanner(139), 
    Scanner(140), 
    DefensiveMatrix(141), 
    PsiStorm(142), 
    Irradiate(143), 
    Plague(144), 
    Consume(145), 
    Ensnare(146), 
    StasisField(147), 
    Hallucianation1(148), 
    Hallucination2(149), 
    ResetCollision1(150), 
    ResetCollision2(151), 
    Patrol(152), 
    CTFCOPInit(153), 
    CTFCOP1(154), 
    CTFCOP2(155), 
    ComputerAI(156), 
    AtkMoveEP(157), 
    HarassMove(158), 
    AIPatrol(159), 
    GuardPost(160), 
    RescuePassive(161), 
    Neutral(162), 
    ComputerReturn(163), 
    InitPsiProvider(164), 
    SelfDestrucing(165), 
    Critter(166), 
    HiddenGun(167), 
    OpenDoor(168), 
    CloseDoor(169), 
    HideTrap(170), 
    RevealTrap(171), 
    Enabledoodad(172), 
    Disabledoodad(173), 
    Warpin(174), 
    Medic(175), 
    MedicHeal1(176), 
    HealMove(177), 
    MedicHoldPosition(178), 
    MedicHeal2(179), 
    Restoration(180), 
    CastDisruptionWeb(181), 
    CastMindControl(182), 
    WarpingDarkArchon(183), 
    CastFeedback(184), 
    CastOpticalFlare(185), 
    CastMaelstrom(186), 
    JunkYardDog(187), 
    Fatal(188), 
    None(189), 
    Unknown(190);

    int orderId;

    Order(int num){
      orderId = num;
    }

    public int getNumValue(){
      return orderId;
    }

	}

  /**
	 * StarCraftCommand (commands)
	 */
  public enum StarCraftCommand {
		none(0),
		attackMove(1),
		attackUnit(2),
		rightClick(3),
		rightClickUnit(4),
		train(5),
		build(6),
		buildAddon(7),
		research(8),
		upgrade(9),
		stop(10),
		holdPosition(11),
		patrol(12),
		follow(13),
		setRallyPosition(14),
		setRallyUnit(15),
		repair(16),
		morph(17),
		burrow(18),
		unburrow(19),
		siege(20),
		unsiege(21),
		cloak(22),
		decloak(23),
		lift(24),
		land(25),
		load(26),
		unload(27),
		unloadAll(28),
		unloadAllPosition(29),
		cancelConstruction(30),
		haltConstruction(31),
		cancelMorph(32),
		cancelTrain(33),
		cancelTrainSlot(34),
		cancelAddon(35),
		cancelResearch(36),
		cancelUpgrade(37),
		useTech(38),
		useTechPosition(39),
		useTechTarget(40),
		gameSpeed(41),
		screenPosition(42),
		lineMap(43),
		lineScreen(44),
		circleMap(45),
		circleScreen(46),
		rectMap(47),
		rectScreen(48),
		boxMap(49),
		boxScreen(50),
		color(51),
		leaveGame(52),
		sayHello(53),
		sayGG(54);
  
    int starcraftCommandId;

    StarCraftCommand(int num) {
      starcraftCommandId = num;
    }

    public int getNumValue() {
      return starcraftCommandId;
    }

  }


}

