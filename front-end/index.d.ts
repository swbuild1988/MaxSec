declare module 'vue/types/vue' {
    interface Vue {
        [key: string]: any
    }

    interface VueRouter {
        [key: string]: any
    }
}
declare global {
    interface Date {
        format(format: string): string;
    }
} 
export {}