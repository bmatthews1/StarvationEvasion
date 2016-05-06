package starvationevasion.common.gamecards;

import starvationevasion.common.EnumRegion;

/**
 * Title: {@value #TITLE}<br><br>
 * Game Text: {@value #TEXT}<br><br>
 *
 * Votes Required: Automatic<br><br>
 *
 * Model Effects: The fact that such cards as this exist, implies that every player's
 * hands must, in theory, be able to be viewed during any part of the game.
 */

public class Policy_SharedKnowledge extends GameCard
{

  public static final String TITLE = "Shared Knowledge";

  public static final String TEXT = 
      "Look at target player\'s hand. " +
      "You may play one card from the revealed hand as though it is in your hand. " +
      "Pay that player $10 million.";

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTitle() {return TITLE;}

  /**
   * {@inheritDoc}
   */
  @Override
  public String getGameText() {return TEXT;}
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int actionPointCost() {return 3;}

  /**
   * {@inheritDoc}
   */
  @Override
  public EnumRegion[] getValidTargetRegions()
  {
    return EnumRegion.US_REGIONS;
  }
}