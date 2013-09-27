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
import mx.controls.DateField;
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

public class singleNodeTQWidow extends mx.containers.TitleWindow
{
	public function singleNodeTQWidow() {}

	[Bindable]
	public var fieldVal : mx.validators.StringValidator;
	[Bindable]
	public var timeFormatter : mx.formatters.DateFormatter;
	[Bindable]
	public var labelHBox : mx.containers.HBox;
	[Bindable]
	public var leftDatePicker : mx.controls.Label;
	[Bindable]
	public var arithmeticOpLabel : mx.controls.Label;
	[Bindable]
	public var entityLabel : mx.controls.Label;
	[Bindable]
	public var relationalOpsLabel : mx.controls.Label;
	[Bindable]
	public var lb5 : mx.controls.Label;
	[Bindable]
	public var lb6 : mx.controls.Label;
	[Bindable]
	public var myBox : mx.containers.HBox;
	[Bindable]
	public var datePicker : mx.controls.DateField;
	[Bindable]
	public var arithmaticOpsCb : mx.controls.ComboBox;
	[Bindable]
	public var firstComboPlace : mx.containers.VBox;
	[Bindable]
	public var attributesCb : mx.controls.ComboBox;
	[Bindable]
	public var relationalOpsCb : mx.controls.ComboBox;
	[Bindable]
	public var timeInputBox : mx.controls.TextInput;
	[Bindable]
	public var timeIntervalCb : mx.controls.ComboBox;
	[Bindable]
	public var columnLabel : mx.controls.Label;
	[Bindable]
	public var customColumnName : mx.controls.TextInput;
	[Bindable]
	public var timeIntervalCb6 : mx.controls.ComboBox;
	[Bindable]
	public var lb7 : mx.controls.Label;
	[Bindable]
	public var submitButton1 : mx.controls.Button;
	[Bindable]
	public var cancelButton : mx.controls.Button;

	public var _bindingsByDestination : Object;
	public var _bindingsBeginWithWord : Object;

include "E:/workplace/AdvancedQueryForJoin_NewCP/flexclient/advQuery/dag/Components/singleNodeTQWidow.mxml:7,694";

}}
