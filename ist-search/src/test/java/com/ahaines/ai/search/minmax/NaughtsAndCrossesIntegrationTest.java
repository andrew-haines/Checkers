package com.ahaines.ai.search.minmax;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ahaines.ai.search.game.GameFinishedException;
import com.ahaines.ai.search.game.TurnDrivenGameService;
import com.ahaines.ai.search.minmax.model.MinMaxState;
import com.ahaines.ai.search.minmax.model.TurnDrivenState;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.SimpleTurn;
import com.ahaines.ai.search.minmax.model.TurnDrivenState.Turn;
import com.ahaines.ai.search.minmax.service.AlphaBetaPrunningSuccessorService;
import com.ahaines.ai.search.minmax.service.MinMaxSuccessorService;
import com.ahaines.ai.search.minmax.service.TurnDrivenGoalService;
import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.service.CachedSuccessorService;
import com.ahaines.ai.search.service.SuccessorService;
import com.ahaines.ai.search.service.heurstic.service.CostFunctionService;
import com.ahaines.ai.search.service.heurstic.service.HeuristicSearchService;
import com.ahaines.ai.search.service.heurstic.service.HeuristicSearchService.HeuristicSearchServiceBuilder;
import com.ahaines.boardgame.model.Board;
import com.ahaines.boardgame.model.Piece;
import com.ahaines.boardgame.model.PieceDescription;
import com.ahaines.boardgame.model.PieceLookup;
import com.ahaines.boardgame.model.Player;
import com.ahaines.boardgame.model.Player.PlayerType;
import com.ahaines.boardgame.model.PlayerId;
import com.ahaines.boardgame.model.PlayerLookup;
import com.ahaines.boardgame.model.Position;
import com.ahaines.boardgame.model.SimpleArrayBackedBoard;

/**
 * This tests the min max algorithm using an implementation of the naughts and crosses game.
 * 
 * @author andrewhaines
 *
 */
public class NaughtsAndCrossesIntegrationTest {

	private static final String PLAYER_1_NAME = "O";
	private static final String PLAYER_2_NAME = "X";
	private static final int[] FIRST_TEST = new int[]{1, -1, 0, 1, 1, -1, -1, 0, 0};
	private static final int[] FIRST_TEST_WON = new int[]{1, -1, 0, 1, 1, -1, -1, 0, 1};
	private static final int[] SECOND_TEST = new int[]{1, 1, 0, 1, -1, 0, -1, -1, 1};
	private static final int[] SECOND_TEST_RESULT = new int[]{1, 1, 1, 1, -1, 0, -1, -1, 1};
	private static final int[] THIRD_TEST = new int[]{1, -1, -1, 1, 0, 0, 0, 0, 0};
	private static final int[] THIRD_TEST_RESULT = new int[]{1, -1, -1, 1, 0, 0, 1, 0, 0};
	
	private static final int[] FOURTH_TEST = new int[]{1, 0, -1, 1, 0, 0, -1, 0, 0};
	private static final int[] FOURTH_TEST_RESULT1 = new int[]{1, 0, -1, 1, 1, 0, -1, 0, 0};
	private static final int[] FOURTH_TEST_RESULT2 = new int[]{1, 0, -1, 1, 1, 1, -1, 0, -1};
	private static final int[] INITIAL_START_STATE = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
	private static final int[] OPTIMAL_OPENING_MOVE = new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0};
	private static final int[] INIT_TEST_STATE1 = new int[]{1, 1, 0, 0, -1, 0, 0, 0, 0};
	private static final int[] INIT_TEST_STATE2 = new int[]{1, 1, -1, 0, -1, 0, 1, 0, 0};
	private static final Object INIT_TEST_STATE3 = new int[]{1, 1, -1, -1, -1, 1, 1, 0, 0};
	private NaughtsAndCrossesGame game;
	private HeuristicSearchService<MinMaxState<OXBoard>> oxSearchService;
	private OXPlayerLookup playerLookup;
	private OXCostFunctionService oxCostFunctionService;
	
	@Before
	public void before(){
		
		Player<OXPiece> player1 = new Player<OXPiece>(new OXPlayerId(SimpleTurn.MAX.getId(), PLAYER_1_NAME), PlayerType.COMPUTER, new ArrayDeque<Piece<OXPiece>>());
		Player<OXPiece> player2 = new Player<OXPiece>(new OXPlayerId(SimpleTurn.MIN.getId(), PLAYER_2_NAME), PlayerType.HUMAN, new ArrayDeque<Piece<OXPiece>>()); // in this example player 2 will be a human implemented by the test case!
		playerLookup = new OXPlayerLookup(player1, player2);
		OXSuccessorService oxSuccessorService = new OXSuccessorService(playerLookup);
		oxCostFunctionService = new OXCostFunctionService(playerLookup);
		MinMaxSuccessorService<OXBoard> minMaxSuccessorService = new MinMaxSuccessorService<OXBoard>(oxSuccessorService, oxCostFunctionService, oxCostFunctionService);
		AlphaBetaPrunningSuccessorService<OXBoard> abPrunService = new AlphaBetaPrunningSuccessorService<OXBoard>(minMaxSuccessorService);
		CachedSuccessorService<MinMaxState<OXBoard>> cache = new CachedSuccessorService<MinMaxState<OXBoard>>(abPrunService);
		oxSearchService = new HeuristicSearchServiceBuilder<MinMaxState<OXBoard>>(cache)
				.build();
	}
	
	private NaughtsAndCrossesGame getGame(int[] startState){
		return new NaughtsAndCrossesGame(new TurnDrivenGameService<OXBoard>(oxSearchService, new OXBoard(OXBoard.getNextId(), startState, playerLookup, SimpleTurn.MIN), oxCostFunctionService, Integer.MAX_VALUE), playerLookup);
	}
	
	@Test
	public void givenNearlyWonState_whenCallingGetNextMove_thenWinningStatePickedCorrectly() throws GameFinishedException{
		NaughtsAndCrossesGame game = getGame(FIRST_TEST);
		System.out.println(game.getCurrentState());
		OXBoard board = game.getNextMove();
		
		System.out.println(board);
		
		assertThat(board.getInternalArrayedState(), is(equalTo(FIRST_TEST_WON)));
		assertThat(game.isWon(), is(equalTo(true)));
	}
	
	@Test
	public void givenWinningMoveAvailableAndOpponentMoveToBlock_whenCallingGetNextMove_thenPositionPickedCorrectly() throws GameFinishedException{
		NaughtsAndCrossesGame game = getGame(SECOND_TEST);
		
		OXBoard board = game.getNextMove();
		
		assertThat(board.getInternalArrayedState(), is(equalTo(SECOND_TEST_RESULT)));
	}
	
	@Test
	public void givenWinningMoveAvailable_whenCallingGetNextMove_thenWinningMoveIsPicked() throws GameFinishedException{
		NaughtsAndCrossesGame game = getGame(THIRD_TEST);
		
		OXBoard board = game.getNextMove();
		
		assertThat(board.getInternalArrayedState(), is(equalTo(THIRD_TEST_RESULT)));
	}
	
	@Test
	@Ignore
	public void givenOpponentCouldWin_whenCallingGetNextMove_OpponentBlocked() throws GameFinishedException{
		NaughtsAndCrossesGame game = getGame(FOURTH_TEST);
		
		OXBoard board = game.getNextMove();
		
		assertThat(board.getInternalArrayedState(), is(equalTo(FOURTH_TEST_RESULT1)));
		
		game.playMove(new Position(2, 2));
		
		board = game.getNextMove();
		
		assertThat(board.getInternalArrayedState(), is(equalTo(FOURTH_TEST_RESULT2)));
	}
	
	@Test
	public void givenInitialSpace_whenCallingNextMove_thenOptimalPositionPicked() throws GameFinishedException{
		NaughtsAndCrossesGame game = getGame(INITIAL_START_STATE);
		OXBoard board = game.getNextMove();
		
		assertThat(board.getInternalArrayedState(), is(equalTo(OPTIMAL_OPENING_MOVE)));
	}
	
	@Test(expected=IllegalStateException.class)
	public void givenInitialState_whenIllegalMovePlayed_thenExceptionThrown() throws GameFinishedException{
		NaughtsAndCrossesGame game = getGame(INITIAL_START_STATE);
		game.getNextMove();
		
		game.playMove(new Position(0, 0));
		
	}
	
	@Test
	@Ignore
	public void givenInitialState_whenPlayingAFewMoves_thenCorrectCompStatesPicked() throws GameFinishedException{
		NaughtsAndCrossesGame game = getGame(INITIAL_START_STATE);
		OXBoard board = game.getNextMove();
		
		game.playMove(new Position(1,1));
		
		board = game.getNextMove();
		
		assertThat(board.getInternalArrayedState(), is(equalTo(INIT_TEST_STATE1)));
		
		game.playMove(new Position(0,2));
		
		board = game.getNextMove();
		
		assertThat(board.getInternalArrayedState(), is(equalTo(INIT_TEST_STATE2)));
		
		game.playMove(new Position(1,0));
		
		board = game.getNextMove();
		
		assertThat(board.getInternalArrayedState(), is(equalTo(INIT_TEST_STATE3)));
		
	}
}

class NaughtsAndCrossesGame{
	
	private final TurnDrivenGameService<OXBoard> gameService;
	private final PlayerLookup<OXPiece> playerLookup;
	
	NaughtsAndCrossesGame(TurnDrivenGameService<OXBoard> gameService, PlayerLookup<OXPiece> playerLookup){
		this.gameService = gameService;
		this.playerLookup = playerLookup;
	}

	public OXBoard getCurrentState() {
		return gameService.getCurrentState();
	}

	public Boolean isWon() {
		return gameService.isStateWon();
	}

	public OXBoard getNextMove() throws GameFinishedException {
		boolean isWon = gameService.playNextMove();
		
		return gameService.getCurrentState();
	}
	
	public OXBoard playMove(Position position) throws GameFinishedException {
		OXBoard board = gameService.getCurrentState();
		OXBoard nextMove = new OXBoard(board, OXBoard.getNextId(), board.getTurn().nextTurn());
		
		Piece<OXPiece> newPiece = new Piece<OXPiece>(position, new OXPiece(playerLookup.getPlayer(nextMove.getTurn().getId())));
		nextMove.addPiece(newPiece);
		
		gameService.playNextMove(nextMove);
		
		return gameService.getCurrentState();
	}
}

class OXBoard extends SimpleArrayBackedBoard<OXPiece, Board.SimpleStats<OXPiece>> implements Identifiable, TurnDrivenState{
	private static int NEXT_ID = 0;
	private final int id;
	private final Turn turn;
	
	OXBoard(SimpleArrayBackedBoard<OXPiece, SimpleStats<OXPiece>> board, int id, Turn turn) {
		super(board);
		this.id = id;
		this.turn = turn;
	}

	OXBoard(int id, OXPlayerLookup playerLookup, Turn turn) {
		super(new OXPieceLookup(playerLookup), 3, 3);
		this.turn = turn;
		this.id = id;
	}
	
	OXBoard(int id, int[] state, OXPlayerLookup playerLookup, Turn turn) {
		super(state, new OXPieceLookup(playerLookup), 3, 3);
		this.turn = turn;
		this.id = id;
	}

	static int getNextId() {
		return NEXT_ID++;
	}

	public int getId() {
		return id;
	}

	public Turn getTurn() {
		return turn;
	}

	public void removePiece(Piece<OXPiece> piece) {
		throw new UnsupportedOperationException("removing a piece is not supported in OX's");
	}

	public int[] getInternalArrayedState() {
		return state;
	}

	@Override
	protected SimpleStats<OXPiece> createStat() {
		return new SimpleStats<OXPiece>();
	}

	@Override
	protected Board.SimpleStats<OXPiece> createStat(SimpleStats<OXPiece> stat) {
		return new SimpleStats<OXPiece>(stat);
	}
}
class OXPiece extends PieceDescription<OXPiece>{

	public OXPiece(Player<OXPiece> player) {
		super(player);
	}
	
}

class OXSuccessorService implements SuccessorService<OXBoard>{
	
	private final PlayerLookup<OXPiece> playerLookup;
	
	OXSuccessorService(PlayerLookup<OXPiece> playerLookup){
		this.playerLookup = playerLookup;
	}

	public Iterable<OXBoard> getSuccessors(OXBoard state) {
		List<OXBoard> successors = new LinkedList<OXBoard>();
        for (int x = 0; x < 3; x++) {
           for (int y = 0; y < 3; y++) {
        	   Iterator<Piece<OXPiece>> piecesAtPlacement = state.getPiecesAtPlacement(new Position(x, y)).iterator();
              if (!piecesAtPlacement.hasNext()) { /* no pieces here */
            	 Turn nextTurn = state.getTurn().nextTurn();
            	 OXBoard newBoard = new OXBoard(state, OXBoard.getNextId(), nextTurn);
            	 newBoard.addPiece(new Piece<OXPiece>(new Position(x, y), new OXPiece(playerLookup.getPlayer(nextTurn.getId()))));
                
                 successors.add(newBoard); 
              }
           }
        }
        return successors;
	}
}
class OXCostFunctionService implements CostFunctionService<OXBoard>, TurnDrivenGoalService<OXBoard>{

	private final PlayerLookup<OXPiece> playerLookup;
	
	public OXCostFunctionService(PlayerLookup<OXPiece> playerLookup){
		this.playerLookup = playerLookup;
	}
	public int calculateCost(OXBoard state) {
		
		if (isStateWon(state, state.getTurn().getId())){
			return 1;
		} else if (isStateWon(state, state.getTurn().nextTurn().getId())){
			return -1;
		} else{
			return 0;
		}
	}

	public boolean determineIfStateWon(OXBoard state, Player<?> player) {
		int[] s = state.getInternalArrayedState();
		int p = player.getPlayerId().getId();
		boolean b = (s[0] == p && s[1] == p && s[2] == p)
			     || (s[3] == p && s[4] == p && s[5] == p)
			     || (s[6] == p && s[7] == p && s[8] == p)
			     || (s[0] == p && s[3] == p && s[6] == p)
			     || (s[1] == p && s[4] == p && s[7] == p)
			     || (s[2] == p && s[5] == p && s[8] == p)
			     || (s[0] == p && s[4] == p && s[8] == p)
			     || (s[2] == p && s[4] == p && s[6] == p);
			     return b;
	}
	public boolean isStateWon(OXBoard state) {
		return determineIfStateWon(state, playerLookup.getPlayer(state.getTurn().getId()));
	}
	public boolean isStateWon(OXBoard state, int playerId) {
		return determineIfStateWon(state, playerLookup.getPlayer(playerId));
	}
}

class OXPieceLookup implements PieceLookup<OXPiece>{

	private final PlayerLookup<OXPiece> playerLookup;
	
	OXPieceLookup(PlayerLookup<OXPiece> playerLookup){
		this.playerLookup = playerLookup;
	}
	public OXPiece getPiece(int id) {
		return new OXPiece(playerLookup.getPlayer(id));
	}
	public Collection<OXPiece> getAllPieces() {
		Collection<OXPiece> allPieces = new LinkedList<OXPiece>();
		for (Player<OXPiece> player: playerLookup.getAllPlayers()){
			allPieces.add(getPiece(player.getPlayerId().getId()));
		}
		
		return allPieces;
	}
	
}

class OXPlayerLookup implements PlayerLookup<OXPiece>{

	private final Player<OXPiece> player1;
	private final Player<OXPiece> player2;
	
	public OXPlayerLookup(Player<OXPiece> player1, Player<OXPiece> player2) {
		this.player1 = player1;
		this.player2 = player2;
	}

	public Player<OXPiece> getPlayer(int id) {
		if (player1.getPlayerId().getId() == id){
			return player1;
		} else if (player2.getPlayerId().getId() == id){
			return player2;
		} else{
			throw new IllegalArgumentException("Unknown player with id: "+id);
		}
	}

	public Collection<Player<OXPiece>> getAllPlayers() {
		return Arrays.asList(player1, player2);
	}
}
class OXPlayerId implements PlayerId{

	public final int id;
	public final String playerName;
	
	public OXPlayerId(int id, String playerName){
		this.id = id;
		this.playerName = playerName;
	}
	
	public int getId() {
		return id;
	}

	public String getPlayerName() {
		return playerName;
	}
	
}
