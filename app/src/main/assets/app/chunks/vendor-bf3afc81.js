function p(){}function I(t,e){for(const n in e)t[n]=e[n];return t}function G(t){return t&&typeof t=="object"&&typeof t.then=="function"}function T(t){return t()}function z(){return Object.create(null)}function y(t){t.forEach(T)}function J(t){return typeof t=="function"}function K(t,e){return t!=t?e==e:t!==e||t&&typeof t=="object"||typeof t=="function"}let x;function ft(t,e){return x||(x=document.createElement("a")),x.href=e,t===x.href}function Q(t){return Object.keys(t).length===0}function dt(t,e,n,r){if(t){const c=B(t,e,n,r);return t[0](c)}}function B(t,e,n,r){return t[1]&&r?I(n.ctx.slice(),t[1](r(e))):n.ctx}function _t(t,e,n,r){if(t[2]&&r){const c=t[2](r(n));if(e.dirty===void 0)return c;if(typeof c=="object"){const o=[],u=Math.max(e.dirty.length,c.length);for(let s=0;s<u;s+=1)o[s]=e.dirty[s]|c[s];return o}return e.dirty|c}return e.dirty}function ht(t,e,n,r,c,o){if(c){const u=B(e,n,r,o);t.p(u,c)}}function mt(t){if(t.ctx.length>32){const e=[],n=t.ctx.length/32;for(let r=0;r<n;r++)e[r]=-1;return e}return-1}function pt(t){return t==null?"":t}let k=!1;function R(){k=!0}function U(){k=!1}function W(t,e,n,r){for(;t<e;){const c=t+(e-t>>1);n(c)<=r?t=c+1:e=c}return t}function V(t){if(t.hydrate_init)return;t.hydrate_init=!0;let e=t.childNodes;if(t.nodeName==="HEAD"){const i=[];for(let l=0;l<e.length;l++){const f=e[l];f.claim_order!==void 0&&i.push(f)}e=i}const n=new Int32Array(e.length+1),r=new Int32Array(e.length);n[0]=-1;let c=0;for(let i=0;i<e.length;i++){const l=e[i].claim_order,f=(c>0&&e[n[c]].claim_order<=l?c+1:W(1,c,_=>e[n[_]].claim_order,l))-1;r[i]=n[f]+1;const a=f+1;n[a]=i,c=Math.max(a,c)}const o=[],u=[];let s=e.length-1;for(let i=n[c]+1;i!=0;i=r[i-1]){for(o.push(e[i-1]);s>=i;s--)u.push(e[s]);s--}for(;s>=0;s--)u.push(e[s]);o.reverse(),u.sort((i,l)=>i.claim_order-l.claim_order);for(let i=0,l=0;i<u.length;i++){for(;l<o.length&&u[i].claim_order>=o[l].claim_order;)l++;const f=l<o.length?o[l]:null;t.insertBefore(u[i],f)}}function X(t,e){if(k){for(V(t),(t.actual_end_child===void 0||t.actual_end_child!==null&&t.actual_end_child.parentElement!==t)&&(t.actual_end_child=t.firstChild);t.actual_end_child!==null&&t.actual_end_child.claim_order===void 0;)t.actual_end_child=t.actual_end_child.nextSibling;e!==t.actual_end_child?(e.claim_order!==void 0||e.parentNode!==t)&&t.insertBefore(e,t.actual_end_child):t.actual_end_child=e.nextSibling}else(e.parentNode!==t||e.nextSibling!==null)&&t.appendChild(e)}function yt(t,e,n){k&&!n?X(t,e):(e.parentNode!==t||e.nextSibling!=n)&&t.insertBefore(e,n||null)}function Y(t){t.parentNode.removeChild(t)}function bt(t,e){for(let n=0;n<t.length;n+=1)t[n]&&t[n].d(e)}function Z(t){return document.createElement(t)}function A(t){return document.createTextNode(t)}function gt(){return A(" ")}function xt(){return A("")}function kt(t,e,n,r){return t.addEventListener(e,n,r),()=>t.removeEventListener(e,n,r)}function $t(t,e,n){n==null?t.removeAttribute(e):t.getAttribute(e)!==n&&t.setAttribute(e,n)}function tt(t){return Array.from(t.childNodes)}function et(t){t.claim_info===void 0&&(t.claim_info={last_index:0,total_claimed:0})}function L(t,e,n,r,c=!1){et(t);const o=(()=>{for(let u=t.claim_info.last_index;u<t.length;u++){const s=t[u];if(e(s)){const i=n(s);return i===void 0?t.splice(u,1):t[u]=i,c||(t.claim_info.last_index=u),s}}for(let u=t.claim_info.last_index-1;u>=0;u--){const s=t[u];if(e(s)){const i=n(s);return i===void 0?t.splice(u,1):t[u]=i,c?i===void 0&&t.claim_info.last_index--:t.claim_info.last_index=u,s}}return r()})();return o.claim_order=t.claim_info.total_claimed,t.claim_info.total_claimed+=1,o}function nt(t,e,n,r){return L(t,c=>c.nodeName===e,c=>{const o=[];for(let u=0;u<c.attributes.length;u++){const s=c.attributes[u];n[s.name]||o.push(s.name)}o.forEach(u=>c.removeAttribute(u))},()=>r(e))}function wt(t,e,n){return nt(t,e,n,Z)}function rt(t,e){return L(t,n=>n.nodeType===3,n=>{const r=""+e;if(n.data.startsWith(r)){if(n.data.length!==r.length)return n.splitText(r.length)}else n.data=r},()=>A(e),!0)}function Et(t){return rt(t," ")}function jt(t,e){e=""+e,t.wholeText!==e&&(t.data=e)}function At(t,e,n,r){n===null?t.style.removeProperty(e):t.style.setProperty(e,n,r?"important":"")}function St(t,e=document.body){return Array.from(e.querySelectorAll(t))}let b;function d(t){b=t}function $(){if(!b)throw new Error("Function called outside component initialization");return b}function Nt(t){$().$$.on_mount.push(t)}function qt(t){$().$$.after_update.push(t)}function Ct(t,e){$().$$.context.set(t,e)}const g=[],O=[],w=[],P=[],D=Promise.resolve();let S=!1;function F(){S||(S=!0,D.then(C))}function vt(){return F(),D}function N(t){w.push(t)}const q=new Set;let E=0;function C(){const t=b;do{for(;E<g.length;){const e=g[E];E++,d(e),ct(e.$$)}for(d(null),g.length=0,E=0;O.length;)O.pop()();for(let e=0;e<w.length;e+=1){const n=w[e];q.has(n)||(q.add(n),n())}w.length=0}while(g.length);for(;P.length;)P.pop()();S=!1,q.clear(),d(t)}function ct(t){if(t.fragment!==null){t.update(),y(t.before_update);const e=t.dirty;t.dirty=[-1],t.fragment&&t.fragment.p(t.ctx,e),t.after_update.forEach(N)}}const j=new Set;let h;function it(){h={r:0,c:[],p:h}}function lt(){h.r||y(h.c),h=h.p}function H(t,e){t&&t.i&&(j.delete(t),t.i(e))}function ut(t,e,n,r){if(t&&t.o){if(j.has(t))return;j.add(t),h.c.push(()=>{j.delete(t),r&&(n&&t.d(1),r())}),t.o(e)}}function Mt(t,e){const n=e.token={};function r(c,o,u,s){if(e.token!==n)return;e.resolved=s;let i=e.ctx;u!==void 0&&(i=i.slice(),i[u]=s);const l=c&&(e.current=c)(i);let f=!1;e.block&&(e.blocks?e.blocks.forEach((a,_)=>{_!==o&&a&&(it(),ut(a,1,1,()=>{e.blocks[_]===a&&(e.blocks[_]=null)}),lt())}):e.block.d(1),l.c(),H(l,1),l.m(e.mount(),e.anchor),f=!0),e.block=l,e.blocks&&(e.blocks[o]=l),f&&C()}if(G(t)){const c=$();if(t.then(o=>{d(c),r(e.then,1,e.value,o),d(null)},o=>{if(d(c),r(e.catch,2,e.error,o),d(null),!e.hasCatch)throw o}),e.current!==e.pending)return r(e.pending,0),!0}else{if(e.current!==e.then)return r(e.then,1,e.value,t),!0;e.resolved=t}}function Tt(t,e,n){const r=e.slice(),{resolved:c}=t;t.current===t.then&&(r[t.value]=c),t.current===t.catch&&(r[t.error]=c),t.block.p(r,n)}function zt(t,e){const n={},r={},c={$$scope:1};let o=t.length;for(;o--;){const u=t[o],s=e[o];if(s){for(const i in u)i in s||(r[i]=1);for(const i in s)c[i]||(n[i]=s[i],c[i]=1);t[o]=s}else for(const i in u)c[i]=1}for(const u in r)u in n||(n[u]=void 0);return n}function Bt(t){return typeof t=="object"&&t!==null?t:{}}function Lt(t){t&&t.c()}function Ot(t,e){t&&t.l(e)}function st(t,e,n,r){const{fragment:c,on_mount:o,on_destroy:u,after_update:s}=t.$$;c&&c.m(e,n),r||N(()=>{const i=o.map(T).filter(J);u?u.push(...i):y(i),t.$$.on_mount=[]}),s.forEach(N)}function ot(t,e){const n=t.$$;n.fragment!==null&&(y(n.on_destroy),n.fragment&&n.fragment.d(e),n.on_destroy=n.fragment=null,n.ctx=[])}function at(t,e){t.$$.dirty[0]===-1&&(g.push(t),F(),t.$$.dirty.fill(0)),t.$$.dirty[e/31|0]|=1<<e%31}function Pt(t,e,n,r,c,o,u,s=[-1]){const i=b;d(t);const l=t.$$={fragment:null,ctx:null,props:o,update:p,not_equal:c,bound:z(),on_mount:[],on_destroy:[],on_disconnect:[],before_update:[],after_update:[],context:new Map(e.context||(i?i.$$.context:[])),callbacks:z(),dirty:s,skip_bound:!1,root:e.target||i.$$.root};u&&u(l.root);let f=!1;if(l.ctx=n?n(t,e.props||{},(a,_,...v)=>{const M=v.length?v[0]:_;return l.ctx&&c(l.ctx[a],l.ctx[a]=M)&&(!l.skip_bound&&l.bound[a]&&l.bound[a](M),f&&at(t,a)),_}):[],l.update(),f=!0,y(l.before_update),l.fragment=r?r(l.ctx):!1,e.target){if(e.hydrate){R();const a=tt(e.target);l.fragment&&l.fragment.l(a),a.forEach(Y)}else l.fragment&&l.fragment.c();e.intro&&H(t.$$.fragment),st(t,e.target,e.anchor,e.customElement),U(),C()}d(i)}class Dt{$destroy(){ot(this,1),this.$destroy=p}$on(e,n){const r=this.$$.callbacks[e]||(this.$$.callbacks[e]=[]);return r.push(n),()=>{const c=r.indexOf(n);c!==-1&&r.splice(c,1)}}$set(e){this.$$set&&!Q(e)&&(this.$$.skip_bound=!0,this.$$set(e),this.$$.skip_bound=!1)}}const m=[];function Ft(t,e=p){let n;const r=new Set;function c(s){if(K(t,s)&&(t=s,n)){const i=!m.length;for(const l of r)l[1](),m.push(l,t);if(i){for(let l=0;l<m.length;l+=2)m[l][0](m[l+1]);m.length=0}}}function o(s){c(s(t))}function u(s,i=p){const l=[s,i];return r.add(l),r.size===1&&(n=e(c)||p),s(t),()=>{r.delete(l),r.size===0&&(n(),n=null)}}return{set:c,update:o,subscribe:u}}export{Bt as A,ot as B,I as C,Ft as D,vt as E,ft as F,X as G,p as H,dt as I,ht as J,mt as K,_t as L,pt as M,kt as N,St as O,bt as P,y as Q,O as R,Dt as S,Mt as T,Tt as U,tt as a,$t as b,wt as c,Y as d,Z as e,At as f,yt as g,rt as h,Pt as i,jt as j,gt as k,xt as l,Et as m,it as n,ut as o,lt as p,H as q,Ct as r,K as s,A as t,qt as u,Nt as v,Lt as w,Ot as x,st as y,zt as z};
