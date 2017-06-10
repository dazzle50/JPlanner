/**************************************************************************
 *  Copyright (C) 2017 by Richard Crook                                   *
 *  https://github.com/dazzle50/JPlannerFX                                *
 *                                                                        *
 *  This program is free software: you can redistribute it and/or modify  *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  This program is distributed in the hope that it will be useful,       *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with this program.  If not, see http://www.gnu.org/licenses/    *
 **************************************************************************/

package rjc.jplanner.model;

import java.text.DecimalFormat;
import java.util.ArrayList;

import rjc.jplanner.JPlanner;

/*************************************************************************************************/
/************************* Resources assigned to single task within plan *************************/
/*************************************************************************************************/

public class TaskResources
{
  // structure that contains one task resource assignment
  private class Assignment
  {
    public String tag; // initials or name or org or group or alias or role etc
    public float  max; // 0 (zero) means unlimited
  }

  ArrayList<Assignment> m_assignments; // list of resource assignments in original string format

  /**************************************** constructor ******************************************/
  public TaskResources()
  {
    // empty assignment list
    m_assignments = new ArrayList<Assignment>();
  }

  /**************************************** constructor ******************************************/
  public TaskResources( String text )
  {
    // split text into individual assignments
    m_assignments = new ArrayList<Assignment>();
    String tag;
    String max;
    for ( String part : text.split( "," ) )
    {
      // split part into tag and max assignment
      part = JPlanner.clean( part );
      int bracket = part.indexOf( '[' );
      if ( bracket >= 0 )
      {
        tag = part.substring( 0, bracket ).trim();
        max = part.substring( bracket + 1 ).replaceAll( "]", "" ).trim();
      }
      else
      {
        tag = part;
        max = "0";
      }

      Assignment assignment = new Assignment();
      assignment.tag = tag;
      assignment.max = Float.parseFloat( max );
      m_assignments.add( assignment );
    }
  }

  /**************************************** constructor ******************************************/
  public TaskResources( TaskResources tr, String oldTag, String newTag )
  {
    // replace old tag with new tag
    m_assignments = new ArrayList<Assignment>();
    tr.m_assignments.forEach( oldAssignment ->
    {
      Assignment newAssignment = new Assignment();
      newAssignment.tag = oldAssignment.tag.equals( oldTag ) ? newTag : oldAssignment.tag;
      newAssignment.max = oldAssignment.max;
      m_assignments.add( newAssignment );
    } );

  }

  /***************************************** toString ********************************************/
  @Override
  public String toString()
  {
    // if no assignments, return empty string
    if ( m_assignments.isEmpty() )
      return "";

    StringBuilder str = new StringBuilder();
    DecimalFormat df = new DecimalFormat( "0" );
    df.setMaximumFractionDigits( 4 );

    // build up string equivalent
    for ( Assignment assignment : m_assignments )
    {
      str.append( assignment.tag );
      if ( assignment.max > 0.0 )
      {
        str.append( '[' );
        str.append( df.format( assignment.max ) );
        str.append( ']' );
      }
      str.append( ", " );
    }

    // remove final ", " and return string equivalent
    return str.substring( 0, str.length() - 2 );
  }

  /******************************************** errors *******************************************/
  public static String errors( String text )
  {
    // split text into individual predecessors
    StringBuilder error = new StringBuilder();
    String tag;
    String max;
    for ( String part : text.split( "," ) )
    {
      // if blank part, skip
      part = JPlanner.clean( part );
      if ( part.length() == 0 )
        continue;

      // split part into tag and max assignment
      int bracket = part.indexOf( '[' );
      if ( bracket >= 0 )
      {
        tag = part.substring( 0, bracket ).trim();
        max = part.substring( bracket + 1 ).replaceAll( "]", "" ).trim();
      }
      else
      {
        tag = part;
        max = "1";
      }

      if ( !JPlanner.plan.resources.isTagValid( tag ) )
        error.append( '\'' ).append( tag ).append( "' is not an assignable resource.  " );

      try
      {
        float value = Float.parseFloat( max );
        if ( value <= 0.0 )
          throw new NumberFormatException();
      }
      catch ( Exception exception )
      {
        error.append( '\'' ).append( max ).append( "' is not a valid number for '" ).append( tag ).append( "'.  " );
      }

    }

    if ( error.length() < 1 )
      return null;
    return error.toString();
  }

  /***************************************** containsTag *****************************************/
  public boolean containsTag( String tag )
  {
    // return true if resources assigned to task includes this tag
    for ( Assignment assignment : m_assignments )
      if ( assignment.tag.equals( tag ) )
        return true;

    return false;
  }

  /******************************************** assign *******************************************/
  public void assign( Task task )
  {
    // TODO Auto-generated method stub !!!!!!!!!!!!!!!!!!!!!
    for ( Assignment assignment : m_assignments )
    {
      ArrayList<Resource> res = JPlanner.plan.resources.getResourceList( assignment.tag );

    }

  }

}
