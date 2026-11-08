# Ficha pronta para a Google Play Store

Copiar/colar diretamente na Play Console. Todos os textos abaixo respeitam os limites
de caracteres da Google e a política de privacidade (o Plexo não recolhe dados).

---

## Identidade

| Campo | Valor |
|---|---|
| **Nome da app** (30 máx.) | `Plexo: dois apps em ecrã dividido` *(31 → ver alternativa)* |
| Alternativa segura | `Plexo: dois apps ao mesmo tempo` |
| **Pacote** | `com.melven.plexo` |
| **Versão** | `1.0.0` (versionCode `1`) |
| **Categoria** | Ferramentas (Tools) |
| **Classificação** | Todos (Livre) |
| **Preço** | Gratuito, sem compras integradas |

## Descrição curta (80 máx.)

```
Abra duas apps ao mesmo tempo em ecrã dividido. Pares guardados, widget e zero anúncios.
```

## Descrição completa (4000 máx.)

```
Plexo — dois apps. Um ecrã.

Cansado de repetir o mesmo ritual? Abrir a app, ir aos recentes, manter premido, escolher
"ecrã dividido"… e voltar a fazer tudo quando muda de tarefa?

O Plexo transforma isso num único toque. Escolha duas aplicações, dê um nome ao par
— "Trabalho", "Estudo", "Viagem" — e abra-as lado a lado sempre que quiser.

▸ ABRIR DOIS APPS AO MESMO TEMPO
Escolha a app de cima e a app de baixo. Um botão. As duas abrem em simultâneo em ecrã
dividido. Sem menus, sem passos escondidos.

▸ PARES GUARDADOS
Guarde até 12 combinações com nomes próprios. O Plexo sugere automaticamente nomes curtos
a partir das apps escolhidas.

▸ DOIS MODOS DE VISUALIZAÇÃO
• Ecrã dividido — uma app em cima, outra em baixo (ou lado a lado em tablets).
• Flutuante — duas janelas redimensionáveis, em dispositivos compatíveis.

▸ WIDGET E AJUSTES RÁPIDOS
Um widget 2×2 no ecrã inicial e um tile nos ajustes rápidos abrem o último par usado
a partir de qualquer lado.

▸ SEM ANÚNCIOS. SEM CONTAS. SEM RASTREIO.
O Plexo não pede nome, e-mail ou login. Não tem anúncios, não tem telemetria e não
recolhe dados pessoais. Tudo o que guarda fica no seu telefone.

▸ LEVE E HONESTA
APK de ~1,2 MB. Interface desenhada em Material 3, tema escuro "aurora", com suporte a
"remover animações" do sistema para quem prefere menos movimento.

▸ COMPATIBILIDADE
Android 8.0 (API 26) ou superior. A abertura em ecrã dividido depende do fabricante:
o Plexo tenta sempre o melhor método disponível e recorre a uma abertura sequencial
quando o sistema não permite dividir o ecrã de forma forçada.

Plexo. Dois apps. Um ecrã.
```

## Privacidade

**Frase para a Play Console (Data safety):**

> O Plexo não recolhe nem partilha dados dos utilizadores. Não requer permissões
> sensíveis, não contém SDKs de publicidade e funciona integralmente offline.

**Política de privacidade (texto completo, alojar numa página pública):**

```
POLÍTICA DE PRIVACIDADE — PLEXO
Última atualização: 8 de novembro de 2026

O Plexo não recolhe, transmite, vende nem partilha qualquer dado pessoal.

1. Dados guardados no dispositivo
A app guarda apenas os pares de aplicações que o utilizador cria (nomes e nomes de
pacote) e a última seleção, em armazenamento local privado do Android. Estes dados
nunca saem do telefone e podem ser apagados desinstalando a app.

2. Permissões
O Plexo não pede câmara, microfone, localização, contactos, SMS, armazenamento,
acessibilidade ou acesso à internet. Consulta apenas a lista de apps instaladas com
ícone no launcher, para as poder apresentar na escolha, usando a declaração <queries>
do Android.

3. Terceiros
Não existem SDKs de publicidade, análise ou rastreio.

4. Crianças
A app não recolhe dados de qualquer utilizador, incluindo crianças.

5. Contacto
Para questões de privacidade, utilize a secção de suporte da ficha da app.
```

## Assets (já gerados neste repositório)

| Asset | Ficheiro | Especificação Play |
|---|---|---|
| Ícone | `docs/icon-512.png` | 512×512 PNG |
| Feature graphic | `docs/feature-graphic-1024x500.png` | 1024×500 PNG |
| Capturas de telefone | `docs/screenshots/01-home.png`, `02-seletor.png`, `03-dialogo.png` | 720–1080 px |
| APK | `assets/dist/Plexo-1.0.0-release.apk` | assinado, 1,19 MB |

## Checklist de publicação

- [ ] Criar a app na Play Console (nome, pacote `com.melven.plexo`)
- [ ] Preencher ficha: descrição, categoria, contacto
- [ ] Carregar ícone 512, feature graphic e capturas
- [ ] Questionário *Data safety* → "não recolhe dados"
- [ ] Questionário de classificação de conteúdo → Livre
- [ ] Criar *Internal testing* e carregar o APK/AAB de `assets/dist/`
- [ ] Testar em 3–5 dispositivos (Android 8, 11, 13, 14)
- [ ] Promover para *Production*

> **Nota importante sobre a chave de assinatura:** o keystore usado aqui
> (`plexo-release.jks`, guardado fora do repositório) tem a palavra-passe documentada
> em `SIGNING.txt`. Guarde-o (é o que permite atualizações futuras) ou, melhor ainda,
> gere um novo com o App Signing da Google Play.
