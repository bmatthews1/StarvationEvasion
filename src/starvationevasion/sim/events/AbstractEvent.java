package starvationevasion.sim.events;


import java.util.ArrayList;
import java.util.List;

import starvationevasion.common.EnumFood;
import starvationevasion.common.MapPoint;
import starvationevasion.common.Util;
import starvationevasion.sim.CropData;
import starvationevasion.sim.LandTile;
import starvationevasion.sim.Region;
import starvationevasion.sim.Territory;


/**
 * Defines a special event. Takes a land area type and applies effects to the land region based on event.
 *
 * T is the type of area the event effects (territory, region, etc...)
 */
public abstract class AbstractEvent
{
  private EventGraph eventGraph;	
  private LandTile startTile;
  private ArrayList <LandTile> tiles;	
  private int duration;
  private Territory landArea;
  
  // if you want to use an Event graph for a AbstracteEvent than your methods must use this set of landtiles
  private ArrayList<LandTile> effectedTiles;
  
  
  
  /**
   * Creates a special event object
   *
   * @param eventType ActionType of event this will be
   * @param landArea The land area (Territory, Region, etc...) this event effects
   * @param duration How many simulator years this event lasts
   */
  public AbstractEvent(Territory landArea, int duration)
  {
    
    this.duration = duration;
    this.landArea = landArea;
    
    tiles = landArea.getLandTiles();
  }

  /**
   * Applies effects of the event to the to the landArea. This assumes the event lasts one year (due to the
   * resolution of the simulator). When the effect is applied then the duration is decreased by 1 year.
   */
  public void applyEffects()
  {
    duration -= 1;
  }

  /**
   * Return the longitue and latitude location of the event. This will usually be the center of the land area.
   *
   * @return latitude and longitude stored in a MapPoint.
   */
  public abstract MapPoint getLocation();
 
  public int getDuration()
  {
    return duration;
  }

  public Territory getLandArea()
  {
    return landArea;
  }

  public ArrayList<LandTile> getEffectedTiles()
  {
	  return effectedTiles;
  }
  
  public void destroyFarmEquipment()
  {
    for(LandTile tile : landArea.getLandTiles())
    {
      if(tile.getProductionMultiplier() >= 0.3)
      {
        tile.setProductionMultiplier(tile.getProductionMultiplier() - 0.3);
      }
      else tile.setProductionMultiplier(0);
    }
  }
  
  public void destroyInfrastructure()
  {
    for(LandTile tile : landArea.getLandTiles())
    {
      if(tile.getProductionMultiplier() >= 0.3)
      {
        tile.setProductionMultiplier(tile.getProductionMultiplier() - 0.3);
      }
      else tile.setProductionMultiplier(0);
    }
  }
  
  public void resetMultipers()
  {
	  for(LandTile tile : landArea.getLandTiles())
	  {
	    tile.setProductionMultiplier(1.0);  
	  }
	  
	  
  }
  
  public void causeFlood()
  {
   // Flood flood = new Flood( landArea, duration);
    //TODO: add to events list.
  }
  
  public void wipeOutLandTiles(double percentToWipe)
  {
    List<LandTile> tilesToWipe = landArea.getLandTiles();
    int amtToWipe = (int) (tilesToWipe.size() * percentToWipe);
    int numWiped = 0;
    while(numWiped < amtToWipe)
    {
      tilesToWipe.remove(Util.rand.nextInt(tilesToWipe.size())).setProductionMultiplier(0);
      numWiped++;
    }
    
  }
  
  public void wipeOutCrop(EnumFood crop)
  {
    for(LandTile tile : landArea.getLandTiles())
    {
      if(tile.getCrop().equals(crop))
      {
        tile.setProductionMultiplier(0);
      }
    }
    
  }
  
  public void reduceRainFall()
  {
    double productionMultiplier;
    for(LandTile tile : landArea.getLandTiles())
    {
      EnumFood food = tile.getCrop();
      int foodOrdinal = food.ordinal();
      productionMultiplier = tile.getProductionMultiplier() - 
          (tile.getCropRatings()[foodOrdinal].productionRate() - tile.rateTileForCrop(food, region, 2009, cropData, 0.3).productionRate());
      tile.setProductionMultiplier(productionMultiplier);
    }
  }
  
  private void initEventGraph()
  {  
	 int startIndex = Util.rand.nextInt(tiles.size()); 
	 startTile = tiles.get(startIndex);
	 effectedTiles .add(startTile); 
	 eventGraph = new EventGraph(tiles, startIndex);
  }
  public void getEventSpread(float initProbability, int severity)
  {
	  initEventGraph();
	  
	  eventGraph.createSpreadPattern(initProbability, severity);
	  
  }
  /*
   * EventGraph is one way an event could be spread through out an area.
   * 
   * the class will populate the effectedTile list of the AbstractEvent class 
   * 
   * 
   * 
   * assignNieghbors() populate every nodes neighbor list in graph
   * 
   * 
   * */
  
  
  class EventGraph
  {
	  
	 
	  private EventNode startNode;
	  private ArrayList<EventNode> graph;
	  private ArrayList<LandTile> tiles;
	  
	  public EventGraph(ArrayList<LandTile> tiles, int startIndex)
	  {
		 this.tiles = tiles;
		 for(LandTile tile : tiles)
		 {
			 graph.add(new EventNode(tile));
		 }
		 startNode = graph.get(startIndex);
		 assignNeighbors();
	  }
	  
	  private void assignNeighbors()
	  {
		  ArrayList<EventNode> tmpNieghbors = new ArrayList<EventNode>();
		  EventNode curNode; 
		  for(EventNode node : graph)
		  {
			  for(int i = 0; i< graph.size(); i++)
			  {
				  curNode= graph.get(i);
				  LandTile tmp =curNode.landTile;
				  if(tmp != node.landTile)
				  {
					  float tmpLat =tmp.getLatitude();
					  float tmpLong =tmp.getLongitude();
					  
					  float tileLat = node.landTile.getLatitude();
					  float tileLong = node.landTile.getLongitude();
					 
					  // need to change 100 to actual size of landtile
					  if(distanceBetweenLandTiles(tileLat, tileLong, tmpLat,tmpLong) <= 100 )
					  {
						 
						  tmpNieghbors.add(curNode);
						  
					  }
					 
				  }
			  }
			  node.setNieghbors(tmpNieghbors);
			  tmpNieghbors.clear();
			  
		  }
		 
	  }
	  
	  /*
	   * This 
	   * 
	   * 
	   * */
	  public void createSpreadPattern(float initProbability , int severity)
	  {
		  
		 startNode.spreadEvent(initProbability);
		
		 ArrayList<EventNode> nextNodes = startNode.nieghborsThatNodeEffected;
		  
		 if(nextNodes.size()!=0)
		 {	   
		  while(initProbability > 0)
		  {
			  
			  for(EventNode node : nextNodes)
			  {
				  node.spreadEvent(initProbability);
				  
				  for(EventNode effected : node.nieghborsThatNodeEffected)
				  { 
				    if(!nextNodes.contains(effected))
				    {
				    	nextNodes.add(effected);
				    }
				  }
				  
				  
				  
			  }
			  
			  initProbability = initProbability - (1/(10*severity) );
		  }
		 }
	  }
	  
	 
	  /*
	   * This method returns the distance in kilometers between the centers of landtiles.
	   * this method uses the Haversine Formula to calculate distance. 
	   * 
	   * this is where i found the formula  http://andrew.hedges.name/experiments/haversine/
	   * 
	   * this method may be useful for other uses as well 
	   * 
	   * */
	  
	  private double distanceBetweenLandTiles(float lat1,float long1, float lat2 , float long2)
	  {
		  double distance = 0;
		  double R = 6373.0; //radius of the world 
		  
		  float dlon = long2 - long1 ;
		  float dlat = lat2 - lat1 ;
	   	  double a =  ((Math.sin(dlat/2)) * (Math.sin(dlat/2)))  +( Math.cos(lat1) * Math.cos(lat2) * (Math.sin(dlon/2)) * (Math.sin(dlon/2)));
		  double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) ) ;
		  distance = R * c ;
		  
		  return distance;
		  
	  }
	  /*
	   * this class hold the EventNode 
	   * 
	   * stores the nieghbors of this node
	   * 
	   *  also stores the nodes it transfered the event too (memory ineffeicent should be changed)
	   * 
	   *  
	   * 
	   * */
	  
	  class EventNode
	  {
         LandTile landTile;
		 ArrayList <EventNode> nieghbors;
		 ArrayList <EventNode> nieghborsThatNodeEffected;
		 public EventNode(LandTile landTile)
		 {
			this.landTile = landTile;
		 }
		 public void setNieghbors(ArrayList <EventNode> nieghbors)
		 {
			 this.nieghbors= nieghbors;
			
		 }
		 
		 
		 
		 private void addToEffectedList()
		 {
			effectedTiles.add(this.landTile);
		 }
		 
		 
		 private void spreadEvent(float curProbability)
		 {
			 float randFloat = Util.rand.nextFloat();	
			 for (EventNode node : nieghbors)
			 {	
				 if(randFloat > curProbability)
				 {
				  if(!effectedTiles.contains(node.landTile))
				  {
				  nieghborsThatNodeEffected.add(node);	  
				  node.addToEffectedList();
				  }
				 }
			 }
		 }
		 
	  }
	  
  }
  
  
  
  
}
