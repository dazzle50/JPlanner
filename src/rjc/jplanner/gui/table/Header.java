/**************************************************************************
 *  Copyright (C) 2015 by Richard Crook                                   *
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

package rjc.jplanner.gui.table;

import javafx.geometry.Orientation;

/*************************************************************************************************/
/************************* Horizontal header that shows column titles ****************************/
/*************************************************************************************************/

public class Header extends CellGrid
{
  Orientation m_orientation;

  /**************************************** constructor ******************************************/
  public Header( Table table, Orientation orientation )
  {
    // construct table horizontal header for column titles
    super( table );
    m_orientation = orientation;
  }

  /***************************************** createCell ******************************************/
  @Override
  Cell createCell( int column, int row, int x, int y, int w, int h )
  {
    if ( m_orientation == Orientation.HORIZONTAL )
    {
      // horizontal header only has one row, so if row index not zero don't create cell
      if ( row != 0 )
        return null;

      // create horizontal header cell 
      String txt = m_table.getDataSource().getColumnTitle( column );
      h = (int) m_table.getHorizontalHeaderHeight();
      return new HeaderCell( txt, x, 0, w, h );
    }
    else
    {
      // vertical header only has one column, so if column index not zero don't create cell
      if ( column != 0 )
        return null;

      // create vertical header cell
      String txt = m_table.getDataSource().getRowTitle( row );
      w = (int) m_table.getVerticalHeaderWidth();
      return new HeaderCell( txt, 0, y, w, h );
    }

  }

}