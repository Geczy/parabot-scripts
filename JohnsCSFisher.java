import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import java.util.ArrayList;

import org.parabot.environment.api.interfaces.Paintable;
import org.parabot.environment.api.utils.Timer;
import org.parabot.environment.input.Mouse;
import org.parabot.environment.scripts.Category;
import org.parabot.environment.scripts.Script;
import org.parabot.environment.scripts.ScriptManifest;
import org.parabot.environment.scripts.framework.Strategy;
import org.rev317.api.methods.Game;
import org.rev317.api.methods.Inventory;
import org.rev317.api.methods.Npcs;
import org.rev317.api.methods.Players;
import org.rev317.api.methods.Skill;
import org.rev317.api.wrappers.hud.Item;
import org.rev317.api.wrappers.interactive.Npc;

@ScriptManifest( name = "John's Power Fisher", category = Category.FISHING, description = "Power fishes shrimp", author = "John", version = 0.1, servers = { "PKHonor" } )
public class JohnsCSFisher extends Script implements Paintable
{

	private final ArrayList<Strategy> strategy = new ArrayList<Strategy>();
	public Timer timer = new Timer();

	public int startingExp = Skill.FISHING.getExperience();
	public int startingLvl = Skill.FISHING.getLevel();

	public int currentExp = startingExp;
	public int currentLvl = startingLvl;


	@Override
	public boolean onExecute()
	{
		strategy.add( new Fish() );
		strategy.add( new Drop() );
		strategy.add( new Login() );

		provide( strategy );
		return true;
	}

	public class Fish implements Strategy
	{

		@Override
		public boolean activate()
		{
			return Game.isLoggedIn() && ! Inventory.isFull();
		}


		@Override
		public void execute()
		{
			if( Players.getLocal().getAnimation() != - 1 ) {
				System.out.println( "Still fishing..." );
				sleep( 2000 );
				return;
			}

			final Npc[] shrimp = Npcs.getNearest( 316 );
			if( shrimp[0].isOnScreen() ) {
				shrimp[0].interact( "Net" );
			}

			sleep( 2000 );

			currentExp = Skill.FISHING.getExperience();
			currentLvl = Skill.FISHING.getLevel();
		}

	}

	public class Drop implements Strategy
	{

		@Override
		public boolean activate()
		{
			return Game.isLoggedIn() && Inventory.isFull() && Players.getLocal().getAnimation() == - 1;
		}


		@Override
		public void execute()
		{
			for( Item i: Inventory.getItems( 317 ) ) {
				i.interact( "Drop" );
				sleep( 200 );
			}
		}
	}

	public class Login implements Strategy
	{

		@Override
		public boolean activate()
		{
			return ! Game.isLoggedIn();
		}


		@Override
		public void execute()
		{
			System.out.println( "Logging back in" );
			Mouse.getInstance().click( 464, 292, true );
			sleep( 500 );
			Mouse.getInstance().click( 250, 332, true );
			System.out.println( "Waiting 5 seconds after login" );
			sleep( 6000 );
		}
	}

	private final Color color1 = new Color( 0, 102, 255, 180 );
	private final Color color2 = new Color( 0, 255, 255, 204 );
	private final Color color3 = new Color( 0, 255, 255 );
	private final Color color4 = new Color( 255, 255, 255 );
	private final BasicStroke stroke1 = new BasicStroke( 1 );
	private final Font font1 = new Font( "Verdana", 0, 10 );


	@Override
	public void paint( Graphics g2 )
	{
		Graphics2D g = ( Graphics2D )g2;

		g.setColor( color1 );
		g.fillRect( 308, 350, 184, 104 );
		g.setColor( color2 );
		g.setStroke( stroke1 );
		g.drawRect( 308, 350, 184, 104 );
		g.setColor( color3 );
		g.drawLine( 315, 371, 483, 371 );
		g.setFont( font1 );
		g.setColor( color4 );

		String expHr = NumberFormat.getInstance().format( timer.getPerHour( currentExp - startingExp ) );

		g.drawString( expHr, 392, 448 );
		g.drawString( "Exp / hr", 315, 448 );
		g.drawString( currentLvl + " (+" + ( currentLvl - startingLvl ) + ")", 392, 434 );
		g.drawString( "Lvls", 315, 434 );
		g.drawString( NumberFormat.getInstance().format( currentExp - startingExp ), 392, 406 );
		g.drawString( "Exp gained", 315, 406 );
		g.drawString( timer.toString(), 392, 392 );
		g.drawString( "Time", 315, 392 );

		g.drawString( "-= John's Power Fisher =-", 332, 365 );
	}

}
