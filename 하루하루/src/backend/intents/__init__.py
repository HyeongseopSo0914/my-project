from . import festival, steam, l2m, common, stock 

INTENT_HANDLER = {
    "festival_query": festival.handle,
    "외부활동": festival.handle,
    "steam": steam.handle,
    "스팀게임": steam.handle,
    "stock": stock.handle,
    "주식": stock.handle,
    "리니지": l2m.handle,
    "l2m": l2m.handle,
    "default": common.handle
}