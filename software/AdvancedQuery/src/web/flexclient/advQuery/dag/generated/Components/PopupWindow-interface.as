package Components
{
import flash.accessibility.*;
import flash.debugger.*;
import flash.display.*;
import flash.errors.*;
import flash.events.*;
import flash.external.*;
import flash.filters.*;
import flash.geom.*;
import flash.media.*;
import flash.net.*;
import flash.printing.*;
import flash.profiler.*;
import flash.system.*;
import flash.text.*;
import flash.ui.*;
import flash.utils.*;
import flash.xml.*;
import mx.binding.*;
import mx.containers.HBox;
import mx.containers.TitleWindow;
import mx.containers.VBox;
import mx.controls.Button;
import mx.controls.ComboBox;
import mx.controls.Label;
import mx.controls.TextInput;
import mx.core.ClassFactory;
import mx.core.DeferredInstanceFromClass;
import mx.core.DeferredInstanceFromFunction;
import mx.core.IDeferredInstance;
import mx.core.IFactory;
import mx.core.IPropertyChangeNotifier;
import mx.core.mx_internal;
import mx.formatters.DateFormatter;
import mx.styles.*;
import mx.validators.StringValidator;
import mx.containers.HBox;
import mx.containers.VBox;
import mx.controls.Spacer;
import mx.containers.TitleWindow;

public class PopupWindow extends mx.containers.TitleWindow
{
	public function PopupWindow() {}

	[Bindable]
	public var fieldVal : mx.validators.StringValidator;
	[Bindable]
	public var timeFormatter : mx.formatters.DateFormatter;
	[Bindable]
	public var labelHBox : mx.containers.HBox;
	[Bindable]
	public var lb1 : mx.controls.Label;
	[Bindable]
	public var lb2 : mx.controls.Label;
	[Bindable]
	public var lb3 : mx.controls.Label;
	[Bindable]
	public var lb4 : mx.controls.Label;
	[Bindable]
	public var lb5 : mx.controls.Label;
	[Bindable]
	public var lb6 : mx.controls.Label;
	[Bindable]
	public var myBox : mx.containers.HBox;
	[Bindable]
	public var firstComboPlace : mx.containers.VBox;
	[Bindable]
	public var cb1 : mx.controls.ComboBox;
	[Bindable]
	public var cb2 : mx.controls.ComboBox;
	[Bindable]
	public var secondComboPlace : mx.containers.VBox;
	[Bindable]
	public var cb3 : mx.controls.ComboBox;
	[Bindable]
	public var cb4 : mx.controls.ComboBox;
	[Bindable]
	public var txtInput : mx.controls.TextInput;
	[Bindable]
	public var cb5 : mx.controls.ComboBox;
	[Bindable]
	public var columnLabel : mx.controls.Label;
	[Bindable]
	public var customColumnName : mx.controls.TextInput;
	[Bindable]
	public var cb6 : mx.controls.ComboBox;
	[Bindable]
	public var lb7 : mx.controls.Label;
	[Bindable]
	public var submitButton : mx.controls.Button;
	[Bindable]
	public var cancelButton : mx.controls.Button;

	public var _bindingsByDestination : Object;
	public var _bindingsBeginWithWord : Object;

include "E:/workplace/AdvancedQueryForJoin_NewCP/flexclient/advQuery/dag/Components/PopupWindow.mxml:7,799";

}}
