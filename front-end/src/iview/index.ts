import {
    Vue
} from "vue-property-decorator"
const {
    Row,
    Col,
    Input,
    Select,
    DatePicker,
    TimePicker,
    Option,
    Layout,
    Header,
    Sider,
    Menu,
    MenuItem,
    Breadcrumb,
    BreadcrumbItem,
    Content,
    Icon,
    Submenu,
    Button,
    Table,
    Modal,
    Form,
    FormItem,
    Page,
    Collapse,
    Panel,
    Circle,
    RadioGroup,
    Radio,
    CheckboxGroup,
    Checkbox,
    ButtonGroup,
    Spin,
    Dropdown,
    Poptip,
    Tabs,
    TabPane,
    InputNumber,
    List,
    Tag,
    Badge,
    Carousel,
    CarouselItem
} = require('view-design')

const iview = {
    install: () => {
        Vue.component('Collapse', Collapse)
        Vue.component('Panel', Panel)
        Vue.component('Row', Row)
        Vue.component('Col', Col)
        Vue.component('Input', Input)
        Vue.component('Select', Select)
        Vue.component('DatePicker', DatePicker)
        Vue.component('TimePicker', TimePicker)
        Vue.component('Option', Option)
        Vue.component('Layout', Layout)
        Vue.component('Header', Header)
        Vue.component('Sider', Sider)
        Vue.component('Menu', Menu)
        Vue.component('MenuItem', MenuItem)
        Vue.component('Breadcrumb', Breadcrumb)
        Vue.component('BreadcrumbItem', BreadcrumbItem)
        Vue.component('Content', Content)
        Vue.component('Icon', Icon)
        Vue.component('Submenu', Submenu)
        Vue.component('Button', Button)
        Vue.component('Table', Table)
        Vue.component('Modal', Modal)
        Vue.component('Form', Form)
        Vue.component('FormItem', FormItem)
        Vue.component('Page', Page)
        Vue.component('i-circle', Circle)
        Vue.component('RadioGroup', RadioGroup)
        Vue.component('Radio', Radio)
        Vue.component('CheckboxGroup', CheckboxGroup)
        Vue.component('Checkbox', Checkbox)
        Vue.component('ButtonGroup', ButtonGroup)
        Vue.component('Spin', Spin)
        Vue.component('Dropdown', Dropdown)
        Vue.component('Poptip', Poptip)
        Vue.component('Tabs', Tabs)
        Vue.component('TabPane', TabPane)
        Vue.component('InputNumber', InputNumber)
        Vue.component('List', List)
        Vue.component('Tag', Tag)
        Vue.component('Badge', Badge)
        Vue.component('Carousel', Carousel)
        Vue.component('CarouselItem', CarouselItem)
    }
}

export default iview