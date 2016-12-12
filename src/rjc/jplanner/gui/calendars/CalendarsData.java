/**************************************************************************
 *  Copyright (C) 2016 by Richard Crook                                   *
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

package rjc.jplanner.gui.calendars;

import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import rjc.jplanner.JPlanner;
import rjc.jplanner.command.CommandCalendarSetCycleLength;
import rjc.jplanner.command.CommandCalendarSetExceptions;
import rjc.jplanner.command.CommandCalendarSetValue;
import rjc.jplanner.gui.Colors;
import rjc.jplanner.gui.days.EditorSelectDay;
import rjc.jplanner.gui.table.AbstractCellEditor;
import rjc.jplanner.gui.table.EditorDateTime;
import rjc.jplanner.gui.table.ITableDataSource;
import rjc.jplanner.gui.table.Table;
import rjc.jplanner.gui.table.Table.Alignment;
import rjc.jplanner.model.Calendar;
import rjc.jplanner.model.Date;
import rjc.jplanner.model.Day;

/*************************************************************************************************/
/**************************** Table data source for showing calendars ****************************/
/*************************************************************************************************/

public class CalendarsData implements ITableDataSource
{

  /************************************** getColumnCount *****************************************/
  @Override
  public int getColumnCount()
  {
    // return number of calendars in plan
    return JPlanner.plan.calendarsCount();
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // table row count is max number of normals + SECTION_NORMAL1
    int max = 0;
    for ( int i = 0; i < getColumnCount(); i++ )
      if ( JPlanner.plan.calendar( i ).getNormals().size() > max )
        max = JPlanner.plan.calendar( i ).getNormals().size();

    return max + Calendar.SECTION_NORMAL1;
  }

  /************************************** getColumnTitle *****************************************/
  @Override
  public String getColumnTitle( int columnIndex )
  {
    return "Calendar " + ( columnIndex + 1 );
  }

  /**************************************** getRowTitle ******************************************/
  @Override
  public String getRowTitle( int rowIndex )
  {
    return Calendar.sectionName( rowIndex );
  }

  /************************************* getCellAlignment ****************************************/
  @Override
  public Alignment getCellAlignment( int columnIndex, int rowIndex )
  {
    return Alignment.LEFT;
  }

  /************************************* getCellBackground ***************************************/
  @Override
  public Paint getCellBackground( int columnIndex, int rowIndex )
  {
    // all cells are normal coloured except unused normal section cells
    Calendar cal = JPlanner.plan.calendar( columnIndex );
    if ( rowIndex >= cal.getNormals().size() + Calendar.SECTION_NORMAL1 )
      return Colors.DISABLED_CELL;

    return Colors.NORMAL_CELL;
  }

  /***************************************** getEditor *******************************************/
  @Override
  public AbstractCellEditor getEditor( int columnIndex, int rowIndex )
  {
    // return null if cell is not editable, unused normal section cells
    Calendar cal = JPlanner.plan.calendar( columnIndex );
    if ( rowIndex >= cal.getNormals().size() + Calendar.SECTION_NORMAL1 )
      return null;

    // return editor for table body cell
    switch ( rowIndex )
    {
      case Calendar.SECTION_NAME:
        return new EditorCalendarName( columnIndex, rowIndex );
      case Calendar.SECTION_ANCHOR:
        return new EditorDateTime( columnIndex, rowIndex );
      case Calendar.SECTION_EXCEPTIONS:
        return new EditorCalendarExceptions( columnIndex, rowIndex );
      case Calendar.SECTION_CYCLE_LEN:
        return new EditorCalendarCycleLength( columnIndex, rowIndex );
      default:
        return new EditorSelectDay( columnIndex, rowIndex );
    }
  }

  /****************************************** setValue *******************************************/
  @Override
  public void setValue( int columnIndex, int rowIndex, Object newValue )
  {
    // if new value equals old value, exit with no command
    Object oldValue = getValue( columnIndex, rowIndex );
    if ( newValue.equals( oldValue ) )
      return;

    // special command for setting exceptions & cycle-length, otherwise generic
    Calendar cal = JPlanner.plan.calendar( columnIndex );

    if ( rowIndex == Calendar.SECTION_EXCEPTIONS )
      JPlanner.plan.undostack().push( new CommandCalendarSetExceptions( cal, newValue, oldValue ) );
    else if ( rowIndex == Calendar.SECTION_CYCLE_LEN )
      JPlanner.plan.undostack().push( new CommandCalendarSetCycleLength( cal, (int) newValue, (int) oldValue ) );
    else
      JPlanner.plan.undostack().push( new CommandCalendarSetValue( cal, rowIndex, newValue, oldValue ) );
  }

  /****************************************** getValue *******************************************/
  @Override
  public Object getValue( int columnIndex, int rowIndex )
  {
    return JPlanner.plan.calendar( columnIndex ).getValue( rowIndex );
  }

  /***************************************** getCellText *****************************************/
  @Override
  public String getCellText( int columnIndex, int rowIndex )
  {
    // get value to be displayed
    Object value = getValue( columnIndex, rowIndex );

    // convert dates into strings using plan format
    if ( value instanceof Date )
      return ( (Date) value ).toString( JPlanner.plan.dateFormat() );

    // for day-types display name
    if ( value instanceof Day )
      return ( (Day) value ).name();

    // return cell display text
    return ( value == null ? null : value.toString() );
  }

  /***************************************** getCellFont *****************************************/
  @Override
  public Font getCellFont( int columnIndex, int rowIndex )
  {
    // return cell display font
    return Font.getDefault();
  }

  /**************************************** getCellIndent ****************************************/
  @Override
  public int getCellIndent( int columnIndex, int rowIndex )
  {
    // return cell indent level (0 = no indent)
    return 0;
  }

  /********************************** defaultTableModifications **********************************/
  @Override
  public void defaultTableModifications( Table table )
  {
    // default table modifications
    table.setVerticalHeaderWidth( 80 );
    table.setDefaultColumnWidth( 140 );
  }

}
