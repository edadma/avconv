package ca.hyperreal.avconv

import java.awt.{Graphics2D}
import java.awt.Color._
import java.awt.image.{BufferedImage}
import javax.imageio.ImageIO
import java.io.{InputStream, OutputStream, File}

import sys.process.{Process, ProcessIO}


class Avconv( rate: String, video: String )
{
	var in: OutputStream = _
	var out: InputStream = _
	var err: InputStream = _
	val avconv = Process( s"avconv -y -f image2pipe -r $rate -vcodec png -i - -vcodec libx264 -pix_fmt yuv420p $video.mp4" ).run( 
		new ProcessIO(os => in = os, is => out = is, is => err = is) )
	var img: BufferedImage = null
	
	private def write
	{
		if (img ne null)
		{
			ImageIO.write( img, "PNG", in )			
		}
	}
	
	def frame( width: Int, height: Int ) =
	{
		write
		img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB )
		img.getGraphics.asInstanceOf[Graphics2D]
	}
	
	def done =
	{
		write
		in.flush
		in.close
		
	val result = avconv.exitValue
		
		out.close
		err.close
		result
	}
}