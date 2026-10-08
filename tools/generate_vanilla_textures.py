#!/usr/bin/env python3
"""Generate CyberQuest's hand-edited 1.20.1 vanilla-derived pixel textures.

Source sprites (unaltered, embedded for reproducible/offline builds):
https://github.com/InventivetalentDev/minecraft-assets/tree/1.20.1
  assets/minecraft/textures/item/{paper,map,written_book}.png
  assets/minecraft/textures/block/{spruce_planks,dark_oak_planks}.png

Uses nearest-neighbour pixel art and hand-placed highlights, ink, parchment
grain, leather, timber and sealing wax. No image-generation models are used.
Requires Pillow at build time; no runtime dependencies.
"""
from pathlib import Path
from PIL import Image, ImageDraw
from io import BytesIO
from base64 import b64decode

DEST = Path(__file__).resolve().parent.parent / "src/generated/resources/assets/cyberquest/textures"
SOURCES = {
    "paper": "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAd0lEQVQ4y9WRSwrAIAxEvX1u4x28RO7Qdte1YJnCgFZTP4tCA0EQ35sQnftFee8Tehne9iOp6pyEqQBjPO+GaEjCVIJ5QygiaTi11XhTSQhayc8pCskMzF1UE+CiJ+FvmDt4kzDVhC0JzhBCH2xJCE7BuWQJ/Lwu2PhVdE0qW9YAAAAASUVORK5CYII=",
    "map": "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAeElEQVQ4y2NgGBJg+QqP/yBMtuZXr47/v3V3FWmGwGwFafzz5xMYgwwiyhCYrTCNyBhkYHuH5X+ibcWGQWowDIFpxGUzuitQDCFFMywsMFwAEiBkCCw2cIYBPkNgtuLUjMsQEL37YDlhjdgMgWkkSTOyIWRppDsAABifRorVNHrHAAAAAElFTkSuQmCC",
    "written_book": "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAoklEQVQ4y2NgoDWwFGD+D8NkaYxVEvifoyP5v9RI7r+vBPt/sjSCMEFDkDXCNIEwTBynIdg0wjQQZQiIAdOM7GRChoDYYENABLIL8BkC0wjC0dHRCBeABLZv347VEBAGicM0zpw5E6wWRIMNEBNgBSu4du0ahiHYNMI0w72AyxB8GkEYpAeE4bGBbgguGzE0IgNshhClEZshJGtEN4QsjeQAAFXGCeGVjD6yAAAAAElFTkSuQmCC",
    "spruce_planks": "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAzklEQVQ4y31TMQ7CQAzzT1iYO7HQma0bEku3DuzMjEz8+iqf5JMxgcHNXRJdYifFa5vbcz012vf9/IV//m2ZGnjQIwSdj+s0Hs247swhUCXKKknIR3oH/KyXYwfPut/mQ4diOrufFuLICskzfU6FMd4/KCgg63okVeXD+XkFCVoJ6boguaYGGc8c+Jy9SjX71KN34KPLdnOEbqURKnWr2Wd8dOAcqz3wmO+EAM07tVCFai88F792PNvNNRfgifkf+B5U+gwNkpvr4ZpU+7ED0hH3Ykvad7AAAAAASUVORK5CYII=",
    "dark_oak_planks": "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAzklEQVQ4y31TMQ4CMQzzAxDSsYBuuInlJhATYmBnYuD/bylyJVfGBAZf2iRqYieH5+XQHqd9o31d5y/889+OUwMPeoSg877uxqMZ1505BKpEWSUJ+UjvgJ/zsu3gWfd13nQoprP7aSGOrJA80+dUGOP9g4ICsq5HUlU+nJ9XkKCVkK4LkmtqkPHMgc/Zq1SzTz16Bz66bDdH6FYaoVK3mn3GRwfOsdoDj/lOCNC8UwtVqPbCc/Frx7PdXHMBnpj/ge9Bpc/QILm5Hq5JtR9v52uBCq4zbNAAAAAASUVORK5CYII=",
}
V = {name: Image.open(BytesIO(b64decode(b64))).convert("RGBA")
     for name, b64 in SOURCES.items()}

def save(im, folder, name):
    path = DEST / folder / (name + ".png")
    path.parent.mkdir(parents=True, exist_ok=True)
    im.save(path, format="PNG", optimize=True)

def c(v):
    return max(0, min(255, int(v)))

def wood(which, w, h, shade=0, seed=0):
    src = V[which]
    result = Image.new("RGBA", (w, h))
    p = result.load()
    for y in range(h):
        for x in range(w):
            rgb = src.getpixel(((x+seed)%16,(y+seed*3)%16))
            fleck = ((x*73+y*131+seed*5)%191 == 0)*2
            p[x,y] = tuple(c(k + shade + fleck) for k in rgb[:3]) + (255,)
    return result

def parchment(w,h,seed=0,base=(219,200,158)):
    im = Image.new("RGBA",(w,h))
    p=im.load()
    for y in range(h):
        for x in range(w):
            timber = V["spruce_planks"].getpixel(((x+seed*7)//2%16,(y+seed*9)//2%16))
            paper = V["paper"].getpixel((7+((x//5)%3),7+((y//5+seed)%3)))
            mp = V["map"].getpixel((7+((x//6+seed)%3),7+((y//7)%3)))
            delta = round((sum(timber[:3])/3-94)*.085+(paper[0]-225)*.045+(mp[1]-244)*.024)
            delta += 3 * (((x*31+y*17+seed*37)%97==0)-((x*13+y*41+seed*71)%83==0))
            p[x,y] = tuple(c(v+delta) for v in base)+(255,)
    return im

def paper_sheet(w,h,seed=0,base=(215,193,144),margin=2):
    tex=parchment(w,h,seed,base)
    mask=Image.new("L",(w,h),0)
    p=mask.load()
    for y in range(margin,h-margin):
        for x in range(margin,w-margin):
            edge=margin+int((x*7+y*11+seed*17)%19<4)
            if edge <= x < w-edge and edge <= y < h-edge:
                p[x,y]=255
    im=Image.new("RGBA",(w,h));im.paste(tex,(0,0),mask)
    d=ImageDraw.Draw(im)
    for y in range(margin+1,h-margin-1):
        edge=next((x for x in range(w//2) if p[x,y]),None)
        if edge is not None:
            d.point((edge,y),fill=(143,106,65,255))
            d.point((w-1-edge,y),fill=(165,124,73,255))
    for x in range(margin+1,w-margin-1):
        edge=next((y for y in range(h//2) if p[x,y]),None)
        if edge is not None:
            d.point((x,edge),fill=(156,122,76,255))
            d.point((x,h-1-edge),fill=(137,104,67,255))
    return im

def pin(im,x,y):
    d=ImageDraw.Draw(im)
    d.rectangle((x-2,y-2,x+2,y+2),fill=(39,35,30,255))
    d.rectangle((x-1,y-1,x+1,y+1),fill=(129,133,118,255))
    d.point((x-1,y-1),fill=(200,202,175,255))

def wax(im,x,y,r=3):
    d=ImageDraw.Draw(im)
    d.ellipse((x-r-1,y-r-1,x+r+1,y+r+1),fill=(74,35,29,255))
    d.ellipse((x-r,y-r,x+r,y+r),fill=(149,45,34,255))
    d.arc((x-r+1,y-r+1,x+r-1,y+r-1),20,270,fill=(205,79,56,255))
    d.line((x-1,y,x+1,y),fill=(75,25,24,255))

for name, variant in (("notice_plain",0),("notice_wanted",1),("notice_sealed",2)):
    im=paper_sheet(32,32,seed=3+variant)
    d=ImageDraw.Draw(im)
    pin(im,6,5);pin(im,26,5)
    if variant==0:
        d.rectangle((7,8,24,9),fill=(100,63,36,255))
        for y,w in [(13,19),(16,17),(19,20),(23,13)]:
            d.line((7,y,7+w,y),fill=(100,72,43,255))
    elif variant==1:
        d.line((10,9,22,9),fill=(116,47,37,255),width=2)
        d.rectangle((12,13,19,20),outline=(102,65,42,255))
        d.rectangle((15,14,16,16),fill=(98,57,37,255))
        d.line((11,23,23,23),fill=(101,68,45,255))
        d.line((13,25,21,25),fill=(118,80,49,255))
    else:
        d.line((7,10,23,10),fill=(103,70,40,255),width=2)
        for y,w in [(14,17),(17,14),(20,12)]:
            d.line((8,y,8+w,y),fill=(105,69,43,255))
        wax(im,22,24)
    save(im,"block",name)

back=wood("spruce_planks",16,16,-3,1)
d=ImageDraw.Draw(back)
d.line((0,15,15,15),fill=(88,57,33,255))
d.line((0,0,15,0),fill=(174,125,76,255))
save(back,"block","guild_board_back")
frame=wood("dark_oak_planks",16,16,-1,3)
d=ImageDraw.Draw(frame)
d.line((0,0,15,0),fill=(137,98,50,255),width=2)
d.line((0,15,15,15),fill=(38,27,17,255))
d.line((2,2,13,2),fill=(102,71,40,255))
save(frame,"block","guild_board_frame")

# Bound journal at fixed 448x256 coordinates expected by QuestJournalScreen.
book=Image.new("RGBA",(448,256))
d=ImageDraw.Draw(book)
d.rounded_rectangle((2,4,446,254),radius=13,fill=(20,13,9,205))
book.alpha_composite(wood("dark_oak_planks",436,238,-5,1),(6,8))
d.rounded_rectangle((6,8,442,246),radius=9,outline=(42,26,17,255),width=4)
d.rounded_rectangle((12,13,436,242),radius=6,outline=(146,100,49,255),width=3)
for x,y in [(14,15),(432,15),(14,238),(432,238)]:
    d.rectangle((x-3,y-3,x+3,y+3),fill=(66,43,22,255))
    d.point((x,y),fill=(197,153,83,255))
for i,(xl,xr) in enumerate([(22,216),(232,426)]):
    d.rectangle((xl+4,21,xr+6,235),fill=(62,39,24,255))
    book.alpha_composite(parchment(xr-xl+1,213,11+i,(220,201,157)),(xl,17))
    for off,col in [(0,(136,99,60,255)),(1,(182,142,91,255)),(2,(236,221,178,255))]:
        d.rectangle((xl+off,17+off,xr-off,229-off),outline=col)
    d.line((xl+8,22,xr-7,22),fill=(238,223,184,255))
    d.line((xl+7,226,xr-5,226),fill=(166,133,89,255))
    d.polygon([(xl+2,19),(xl+14,19),(xl+2,31)],fill=(151,111,72,255))
    d.polygon([(xr-2,19),(xr-14,19),(xr-2,31)],fill=(151,111,72,255))
d.rectangle((217,15,231,238),fill=(57,34,24,255))
for x,col in [(218,(20,15,13,255)),(221,(110,69,40,255)),(223,(49,29,22,255)),(227,(116,72,43,255)),(230,(24,17,14,255))]:
    d.line((x,16,x,238),fill=col)
for y in (30,43,208,221):
    d.line((223,y,226,y+2),fill=(194,151,88,255))
save(book,"gui","journal_open")

for selected in [False,True]:
    tab=Image.new("RGBA",(96,24))
    d=ImageDraw.Draw(tab)
    d.polygon([(3,2),(6,0),(90,0),(94,4),(95,22),(0,22),(0,6)],fill=(42,29,19,255))
    tab.alpha_composite(wood("dark_oak_planks",88,17,12 if selected else -2,0 if selected else 2),(4,3))
    d.line((5,3,90,3),fill=(170,123,65,255) if selected else (115,80,48,255),width=2)
    d.line((2,21,93,21),fill=(26,18,13,255),width=2)
    d.line((5,18,91,18),fill=(181,133,73,255) if selected else (95,56,34,255))
    for xx in (9,86):
        d.point((xx,5),fill=(220,170,95,255) if selected else (129,92,51,255))
    save(tab,"gui","journal_tab_selected" if selected else "journal_tab")

for selected in [False,True]:
    entry=Image.new("RGBA",(176,28))
    d=ImageDraw.Draw(entry)
    d.rectangle((0,2,175,27),fill=(64,40,25,255))
    d.rectangle((2,2,173,25),fill=(151,107,65,255))
    entry.alpha_composite(parchment(167,19,7 if selected else 3,
        (226,207,158) if selected else (211,189,146)),(5,4))
    d.rectangle((5,4,172,23),outline=(115,82,48,255))
    d.line((6,5,170,5),fill=(246,226,180,255))
    d.line((6,23,172,23),fill=(123,90,56,255))
    if selected:
        d.rectangle((1,1,174,26),outline=(183,128,48,255),width=2)
        d.line((3,25,173,25),fill=(123,57,39,255))
    save(entry,"gui","journal_entry_selected" if selected else "journal_entry")

notice=Image.new("RGBA",(256,160))
d=ImageDraw.Draw(notice)
d.rounded_rectangle((10,8,250,157),radius=11,fill=(19,13,9,136))
notice.alpha_composite(paper_sheet(248,154,19,(218,194,146),3),(4,1))
d.rectangle((13,10,242,148),outline=(124,84,54,255))
d.rectangle((17,14,238,144),outline=(191,151,99,255))
d.line((30,48,225,48),fill=(172,130,83,255))
d.line((30,117,225,117),fill=(180,138,87,255))
for x,y in [(15,12),(241,12),(15,147),(241,147)]:
    pin(notice,x,y)
save(notice,"gui","guild_notice")

card=Image.new("RGBA",(224,192))
d=ImageDraw.Draw(card)
d.rounded_rectangle((5,6,220,191),radius=9,fill=(21,14,12,190))
card.alpha_composite(wood("dark_oak_planks",210,175,-7,2),(7,8))
d.rectangle((7,8,215,183),outline=(42,28,20,255),width=3)
d.rectangle((11,12,212,181),outline=(160,115,65,255),width=2)
card.alpha_composite(parchment(183,151,3,(223,204,162)),(21,21))
d.rectangle((21,21,204,171),outline=(134,94,60,255),width=2)
d.rectangle((25,25,200,167),outline=(195,153,96,255))
card.alpha_composite(V["written_book"],(33,31))
d.line((38,59,189,59),fill=(183,146,103,255))
d.line((38,151,189,151),fill=(183,146,103,255))
for x,y in [(16,17),(209,17),(16,177),(209,177)]:
    pin(card,x,y)
save(card,"gui","guild_card")

hud=Image.new("RGBA",(210,52))
d=ImageDraw.Draw(hud)
d.rectangle((0,0,209,51),fill=(44,29,18,240))
hud.alpha_composite(wood("spruce_planks",205,46,-12,5),(2,3))
d.rectangle((2,2,207,49),outline=(117,80,45,255),width=2)
hud.alpha_composite(parchment(195,39,8,(222,202,158)),(8,6))
d.rectangle((8,6,201,45),outline=(143,110,68,255))
d.line((14,19,193,19),fill=(180,147,97,255))
save(hud,"gui","quest_hud")

# Approved Guild Contract scroll and Guild Card sprites are checked in under
# src/main/resources/assets/cyberquest/textures/item/ instead of generated here.
# Do not overwrite the user's approved hand-picked inventory icons.
board=Image.new("RGBA",(32,32))
board.alpha_composite(wood("dark_oak_planks",30,28,-9,1),(1,2))
board.alpha_composite(wood("spruce_planks",24,23,-5,2),(4,4))
d=ImageDraw.Draw(board)
d.rectangle((0,1,31,29),outline=(40,27,16,255),width=2)
d.rectangle((3,3,28,26),outline=(149,103,56,255))
for x,y in [(6,5),(17,7),(6,16),(18,18)]:
    d.rectangle((x,y,x+8,y+7),fill=(213,196,147,255),outline=(135,97,61,255))
    d.line((x+2,y+3,x+6,y+3),fill=(88,65,44,255))
save(board,"item","guild_board")

print("Generated 14 vanilla-derived CyberQuest PNG textures:", DEST)
