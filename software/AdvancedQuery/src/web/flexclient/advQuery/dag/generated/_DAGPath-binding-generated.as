
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import mx.core.IPropertyChangeNotifier;
import mx.events.PropertyChangeEvent;
import mx.utils.ObjectProxy;
import mx.utils.UIDUtil;

import Array;
import Boolean;
import int;

class BindableProperty
{
	/**
	 * generated bindable wrapper for property isSelected (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'isSelected' moved to '_398301669isSelected'
	 */

    [Bindable(event="propertyChange")]
    public function get isSelected():Boolean
    {
        return this._398301669isSelected;
    }

    public function set isSelected(value:Boolean):void
    {
    	var oldValue:Object = this._398301669isSelected;
        if (oldValue !== value)
        {
			this._398301669isSelected = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "isSelected", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property sourceExpId (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'sourceExpId' moved to '_108527427sourceExpId'
	 */

    [Bindable(event="propertyChange")]
    public function get sourceExpId():int
    {
        return this._108527427sourceExpId;
    }

    public function set sourceExpId(value:int):void
    {
    	var oldValue:Object = this._108527427sourceExpId;
        if (oldValue !== value)
        {
			this._108527427sourceExpId = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "sourceExpId", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property destinationExpId (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'destinationExpId' moved to '_1181525162destinationExpId'
	 */

    [Bindable(event="propertyChange")]
    public function get destinationExpId():int
    {
        return this._1181525162destinationExpId;
    }

    public function set destinationExpId(value:int):void
    {
    	var oldValue:Object = this._1181525162destinationExpId;
        if (oldValue !== value)
        {
			this._1181525162destinationExpId = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "destinationExpId", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property operatorIndex (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'operatorIndex' moved to '_1174525582operatorIndex'
	 */

    [Bindable(event="propertyChange")]
    public function get operatorIndex():int
    {
        return this._1174525582operatorIndex;
    }

    public function set operatorIndex(value:int):void
    {
    	var oldValue:Object = this._1174525582operatorIndex;
        if (oldValue !== value)
        {
			this._1174525582operatorIndex = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "operatorIndex", oldValue, value));
        }
    }

	/**
	 * generated bindable wrapper for property menuData (public)
	 * - generated setter
	 * - generated getter
	 * - original public var 'menuData' moved to '_604261975menuData'
	 */

    [Bindable(event="propertyChange")]
    public function get menuData():Array
    {
        return this._604261975menuData;
    }

    public function set menuData(value:Array):void
    {
    	var oldValue:Object = this._604261975menuData;
        if (oldValue !== value)
        {
			this._604261975menuData = value;
            dispatchEvent(mx.events.PropertyChangeEvent.createUpdateEvent(this, "menuData", oldValue, value));
        }
    }


}
